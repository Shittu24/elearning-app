package com.ncnmo.aspire.elearning.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncnmo.aspire.elearning.model.User;
import com.ncnmo.aspire.elearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${frontend.token}")
    private String allowedOrigin;

    public OAuth2SuccessHandler(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Extract email from OAuth2 user
        String email = oAuth2User.getAttribute("email");

        // Find the user in the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate JWT token using the user's information
        String token = jwtUtil.generateToken(authentication, user.getId());
        System.out.println("Generated JWT Token: " + token);  // Add this for debugging

        // Redirect to frontend app with the token in the URL
        String redirectUrl = allowedOrigin + token;
        System.out.println("Redirect URL: " + redirectUrl);  // Add this for debugging
        response.sendRedirect(redirectUrl);


    }
}
