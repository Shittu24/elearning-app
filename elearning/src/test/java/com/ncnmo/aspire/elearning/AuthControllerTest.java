//package com.ncnmo.aspire.elearning;
//
//import com.ncnmo.aspire.elearning.controller.AuthController;
//import com.ncnmo.aspire.elearning.payload.LoginRequest;
//import com.ncnmo.aspire.elearning.payload.RegisterRequest;
//import com.ncnmo.aspire.elearning.security.JwtUtil;
//import com.ncnmo.aspire.elearning.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class AuthControllerTest {
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    private static final String ADMIN_REGISTRATION_TOKEN = "V0STw8mtu6vOdcLG8LQSWkfPndDEOMEzZhkyGReReHk=";
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private JwtUtil jwtUtil;
//
//    @InjectMocks
//    private AuthController authController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        authController.setAdminRegistrationToken(ADMIN_REGISTRATION_TOKEN); // Manually set the token
//    }
//
//    @Test
//    void testRegisterUser() {
//        RegisterRequest request = new RegisterRequest();
//        request.setFirstName("John");
//        request.setLastName("Doe");
//        request.setUsername("johndoe");
//        request.setPassword("password");
//        request.setEmail("john@example.com");
//
//        when(userService.findByUsername("johndoe")).thenReturn(Optional.empty());
//
//        ResponseEntity<?> response = authController.registerUser(request);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("User registered successfully!", response.getBody());
//    }
//
//    @Test
//    void testRegisterFirstAdmin() {
//        RegisterRequest request = new RegisterRequest();
//        request.setFirstName("Admin");
//        request.setLastName("User");
//        request.setUsername("admin_user");
//        request.setPassword("adminpassword");
//        request.setEmail("admin@example.com");
//
//        when(userService.findByUsername("admin_user")).thenReturn(Optional.empty());
//
//        ResponseEntity<?> response = authController.registerFirstAdmin(request, ADMIN_REGISTRATION_TOKEN);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("First admin registered successfully!", response.getBody());
//    }
//
//    @Test
//    void testAuthenticateUser() {
//        LoginRequest request = new LoginRequest();
//        request.setUsername("johndoe");
//        request.setPassword("password");
//
//        Authentication authentication = mock(Authentication.class);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        when(jwtUtil.generateToken(any(Authentication.class))).thenReturn("jwtToken");
//
//        ResponseEntity<?> response = authController.authenticateUser(request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    void testRegisterAdmin() {
//        RegisterRequest request = new RegisterRequest();
//        request.setFirstName("New");
//        request.setLastName("Admin");
//        request.setUsername("newadmin");
//        request.setPassword("newpassword");
//        request.setEmail("newadmin@example.com");
//
//        when(userService.findByUsername("newadmin")).thenReturn(Optional.empty());
//
//        // Mock SecurityContext to simulate an admin user being authenticated
//        Authentication authentication = mock(Authentication.class);
//
//        // Create a Collection of GrantedAuthority
//        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
//
//        // Mock the getAuthorities() method to return the mocked authorities
//        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
//        when(authentication.getName()).thenReturn("admin_user");
//
//        // Set the mocked authentication in the security context
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        ResponseEntity<?> response = authController.registerAdmin(request);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("Admin registered successfully!", response.getBody());
//    }
//
//
//
//
//
//
//    @Test
//    void testAuthenticateAdmin() {
//        LoginRequest request = new LoginRequest();
//        request.setUsername("admin_user");
//        request.setPassword("adminpassword");
//
//        Authentication authentication = mock(Authentication.class);
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        when(jwtUtil.generateToken(any(Authentication.class))).thenReturn("jwtToken");
//
//        ResponseEntity<?> response = authController.authenticateUser(request);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//    }
//}
