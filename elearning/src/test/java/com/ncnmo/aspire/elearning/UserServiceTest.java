//package com.ncnmo.aspire.elearning;
//
//import com.ncnmo.aspire.elearning.dto.UserRegistrationDTO;
//import com.ncnmo.aspire.elearning.model.Role;
//import com.ncnmo.aspire.elearning.model.User;
//import com.ncnmo.aspire.elearning.repository.UserRepository;
//import com.ncnmo.aspire.elearning.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//public class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testRegisterUser() {
//        UserRegistrationDTO dto = new UserRegistrationDTO("John", "Doe", "johndoe", "password", "john@example.com");
//        User user = new User();
//        user.setFirstName(dto.getFirstName());
//        user.setLastName(dto.getLastName());
//        user.setUsername(dto.getUsername());
//        user.setEmail(dto.getEmail());
//        user.setRole(Role.ROLE_USER);
//        user.setPassword("encodedPassword");
//
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        User createdUser = userService.registerUser(dto);
//
//        assertNotNull(createdUser);
//        assertEquals(Role.ROLE_USER, createdUser.getRole());
//        assertEquals("encodedPassword", createdUser.getPassword()); // This line tests that the password was encoded
//    }
//
//    @Test
//    void testRegisterAdmin() {
//        UserRegistrationDTO dto = new UserRegistrationDTO("Admin", "User", "admin_user", "password", "admin@example.com");
//        User user = new User();
//        user.setFirstName(dto.getFirstName());
//        user.setLastName(dto.getLastName());
//        user.setUsername(dto.getUsername());
//        user.setEmail(dto.getEmail());
//        user.setRole(Role.ROLE_ADMIN);
//        user.setPassword("encodedPassword");
//
//        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenReturn(user);
//
//        User createdUser = userService.registerAdmin(dto);
//
//        assertNotNull(createdUser);
//        assertEquals(Role.ROLE_ADMIN, createdUser.getRole());
//        assertEquals("encodedPassword", createdUser.getPassword()); // This line tests that the password was encoded
//    }
//
//
//    @Test
//    void testFindByUsername() {
//        User user = new User();
//        user.setUsername("johndoe");
//        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(user));
//
//        Optional<User> foundUser = userService.findByUsername("johndoe");
//
//        assertTrue(foundUser.isPresent());
//        assertEquals("johndoe", foundUser.get().getUsername());
//    }
//}
