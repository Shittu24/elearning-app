package com.ncnmo.aspire.elearning.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
    @Column(unique = true)
    private String username;

    // Password can be null for OAuth2 users
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = true) // Allow null values in the password column
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;  // Default role is USER

    private boolean isOAuth2User = false; // Flag for OAuth2 users

    @Column(unique = true)
    private String oauth2Id;
}
