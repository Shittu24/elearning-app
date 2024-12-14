package com.ncnmo.aspire.elearning.controller;

import com.ncnmo.aspire.elearning.dto.UserRegistrationDTO;
import com.ncnmo.aspire.elearning.model.User;
import com.ncnmo.aspire.elearning.payload.LoginRequest;
import com.ncnmo.aspire.elearning.repository.UserRepository;
import com.ncnmo.aspire.elearning.security.JwtUtil;
import com.ncnmo.aspire.elearning.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${admin.registration.token}")
    private String adminRegistrationToken;

    private boolean isAdminRegistered = false;

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private Validator validator;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody UserRegistrationDTO userRegistrationDTO) {
        logger.info("Registering user with email: {}", userRegistrationDTO.getEmail());

        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Error: Password and Confirm Password do not match!");
        }

        try {
            // Check if the username or email already exists
            if (userService.findByUsername(userRegistrationDTO.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body("Error: Username is already taken!");
            }

            // Register user as ROLE_USER
            userService.registerUser(userRegistrationDTO);

            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);

        } catch (ConstraintViolationException ex) {
            logger.error("ConstraintViolationException: {}", ex.getMessage());
            return ResponseEntity.badRequest().body("Constraint violation occurred: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Unexpected error during registration: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
        }
    }

    @PostMapping("/register-first-admin")
    public ResponseEntity<?> registerFirstAdmin(@Validated @RequestBody UserRegistrationDTO userRegistrationDTO, @RequestParam String token) {
        if (isAdminRegistered) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin registration is no longer allowed.");
        }

        if (!adminRegistrationToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token.");
        }

        if (userService.findByUsername(userRegistrationDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Error: Password and Confirm Password do not match!");
        }

        userService.registerAdmin(userRegistrationDTO);

        isAdminRegistered = true;
        return new ResponseEntity<>("First admin registered successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> registerAdmin(@Validated @RequestBody UserRegistrationDTO userRegistrationDTO) {
        if (userService.findByUsername(userRegistrationDTO.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (!userRegistrationDTO.getPassword().equals(userRegistrationDTO.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Error: Password and Confirm Password do not match!");
        }

        userService.registerAdmin(userRegistrationDTO);

        return new ResponseEntity<>("Admin registered successfully!", HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
        logger.info("Attempting to authenticate user: " + loginRequest.getUsername());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (Exception ex) {
            logger.error("Authentication failed for user: " + loginRequest.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get the user details from the UserService
        User user = userService.findByUsername(loginRequest.getUsername()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        // Generate the token with userId and roles
        String jwt = jwtUtils.generateToken(authentication, user.getId());
        logger.info("Generated JWT token: " + jwt);

        // Return the token as a response
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", jwt);

        return ResponseEntity.ok(tokenMap);  // Return token in JSON format
    }






    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        Map<String, Object> profile = new HashMap<>();

        if (authentication.getPrincipal() instanceof User) {
            // Traditional user retrieved from User entity directly
            User user = (User) authentication.getPrincipal();
            profile.put("userId", user.getId());  // Include user ID
            profile.put("roles", List.of(user.getRole().name()));  // Include roles as list
            profile.put("username", user.getUsername());

        } else if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            // Spring Security user (used in traditional username/password authentication)
            org.springframework.security.core.userdetails.User userDetails =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            // Fetch user from UserRepository by username
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElse(null);

            if (user == null) {
                // If user not found in the database
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token: User not found in the database");
            }

            profile.put("userId", user.getId());  // Fetch userId from the User entity
            profile.put("username", user.getUsername());
            profile.put("roles", userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .collect(Collectors.toList()));
        } else if (authentication.getPrincipal() instanceof OAuth2User) {
            // OAuth2 user (used in social login)
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            // Fetch user by OAuth2 ID or email using UserRepository
            String email = oAuth2User.getAttribute("email");
            User user = userRepository.findByEmail(email)
                    .orElse(null);

            if (user == null) {
                // If user not found in the database
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid token: OAuth2 user not found in the database");
            }

            profile.put("userId", user.getId());  // Include user ID
            profile.put("email", email);
            profile.put("name", oAuth2User.getAttribute("name"));
            profile.put("roles", List.of(user.getRole().name()));  // Convert role to list
        } else {
            // Final fallback: invalid token or user not found
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token: No user associated with the provided token");
        }

        return ResponseEntity.ok(profile);
    }

    public void setAdminRegistrationToken(String adminRegistrationToken) {
        this.adminRegistrationToken = adminRegistrationToken;
    }
}
