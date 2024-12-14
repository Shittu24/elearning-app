//package com.ncnmo.aspire.elearning;
//
//import com.ncnmo.aspire.elearning.controller.UserController;
//import com.ncnmo.aspire.elearning.dto.UserRegistrationDTO;
//import com.ncnmo.aspire.elearning.model.User;
//import com.ncnmo.aspire.elearning.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//class UserControllerTest {
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private PasswordEncoder passwordEncoder;
//
//    @InjectMocks
//    private UserController userController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testUpdateUser() {
//        Long userId = 1L;
//        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO("John", "Doe", "johndoe", "newpassword", "john@example.com");
//        User existingUser = new User();
//        existingUser.setId(userId);
//        existingUser.setFirstName("John");
//        existingUser.setLastName("Doe");
//        existingUser.setUsername("johndoe");
//        existingUser.setEmail("john@example.com");
//        existingUser.setPassword("oldpassword");
//
//        User updatedUser = new User();
//        updatedUser.setId(userId);
//        updatedUser.setFirstName("John");
//        updatedUser.setLastName("Doe");
//        updatedUser.setUsername("johndoe");
//        updatedUser.setEmail("john@example.com");
//        updatedUser.setPassword("encodedPassword");
//
//        when(userService.findById(userId)).thenReturn(Optional.of(existingUser));
//        when(passwordEncoder.encode("newpassword")).thenReturn("encodedPassword");
//        when(userService.saveUser(any(User.class))).thenReturn(updatedUser);
//
//        ResponseEntity<User> responseEntity = userController.updateUser(userId, userRegistrationDTO);
//
//        assertEquals(200, responseEntity.getStatusCodeValue());
//        assertEquals("encodedPassword", responseEntity.getBody().getPassword());
//    }
//
//    @Test
//    void testGetAllUsers() {
//        List<User> users = new ArrayList<>();
//        users.add(new User());
//        when(userService.findAll()).thenReturn(users);
//
//        ResponseEntity<List<User>> response = userController.getAllUsers();
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertFalse(response.getBody().isEmpty());
//    }
//
//    @Test
//    void testGetUserById() {
//        User user = new User();
//        user.setId(1L);
//        when(userService.findById(1L)).thenReturn(Optional.of(user));
//
//        ResponseEntity<User> response = userController.getUserById(1L);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//    }
//
//    @Test
//    void testDeleteUser() {
//        when(userService.findById(1L)).thenReturn(Optional.of(new User()));
//
//        ResponseEntity<Void> response = userController.deleteUser(1L);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//    }
//}
