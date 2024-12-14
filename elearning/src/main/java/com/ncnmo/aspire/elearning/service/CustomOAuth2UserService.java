package com.ncnmo.aspire.elearning.service;//package com.ncnmo.aspire.elearning.service;
//
//import com.ncnmo.aspire.elearning.model.Role;
//import com.ncnmo.aspire.elearning.model.User;
//import com.ncnmo.aspire.elearning.repository.UserRepository;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//import java.util.Optional;
//
//@Service
//public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//    private final UserRepository userRepository;
//
//    public CustomOAuth2UserService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//
//        // Extract user information from OAuth2 response
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String email = (String) attributes.get("email");
//        String oauth2Id = (String) attributes.get("sub");  // OAuth2 ID from Google, usually found in 'sub'
//
//        // Check if the user already exists in the database by email
//        Optional<User> existingUser = userRepository.findByEmail(email);
//        if (existingUser.isEmpty()) {
//            // Create a new user if not found
//            User newUser = new User();
//            newUser.setEmail(email);
//            newUser.setFirstName((String) attributes.get("given_name"));
//            newUser.setLastName((String) attributes.get("family_name"));
//            newUser.setUsername(generateUniqueUsername(email));  // Ensure a valid, unique username
//            newUser.setRole(Role.ROLE_USER);  // Assign default role
//            newUser.setOAuth2User(true);  // Mark as OAuth2 user
//            newUser.setOauth2Id(oauth2Id);  // Store OAuth2 ID
//            userRepository.save(newUser);
//        } else {
//            // If the user already exists, ensure the oauth2Id is updated
//            User user = existingUser.get();
//            if (user.getOauth2Id() == null) {
//                user.setOauth2Id(oauth2Id);  // Update with OAuth2 ID if it's missing
//                userRepository.save(user);
//            }
//        }
//
//        return oAuth2User;  // Return the OAuth2 user
//    }
//
//
//    // Method to generate a unique username that meets the 4-character constraint
//    private String generateUniqueUsername(String email) {
//        String baseUsername = email.split("@")[0];
//        if (baseUsername.length() < 4) {
//            baseUsername = baseUsername + "1234";  // Make it 4 characters if it's too short
//        }
//        String generatedUsername = baseUsername;
//        int suffix = 1;
//        while (userRepository.findByUsername(generatedUsername).isPresent()) {
//            generatedUsername = baseUsername + suffix;
//            suffix++;
//        }
//        return generatedUsername;
//    }
//}


import com.ncnmo.aspire.elearning.model.Role;
import com.ncnmo.aspire.elearning.model.User;
import com.ncnmo.aspire.elearning.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract user information from OAuth2 response
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String oauth2Id = (String) attributes.get("sub");

        // Find or create the user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setFirstName((String) attributes.get("given_name"));
                    newUser.setLastName((String) attributes.get("family_name"));
                    newUser.setUsername(generateUniqueUsername(email));
                    newUser.setRole(Role.ROLE_USER);
                    newUser.setOAuth2User(true);
                    newUser.setOauth2Id(oauth2Id);
                    return userRepository.save(newUser);
                });

        // Build the authorities, and ensure we include the ROLE_USER authority
        Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));

        // Return a new DefaultOAuth2User with user details from DB
        return new DefaultOAuth2User(authorities, attributes, "email");
    }

    // Method to generate a unique username
    private String generateUniqueUsername(String email) {
        String baseUsername = email.split("@")[0];
        if (baseUsername.length() < 4) {
            baseUsername = baseUsername + "1234";
        }
        String generatedUsername = baseUsername;
        int suffix = 1;
        while (userRepository.findByUsername(generatedUsername).isPresent()) {
            generatedUsername = baseUsername + suffix;
            suffix++;
        }
        return generatedUsername;
    }
}
