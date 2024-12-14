//package com.ncnmo.aspire.elearning.service;
//
//import com.ncnmo.aspire.elearning.model.User;
//import com.ncnmo.aspire.elearning.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String usernameOrEmailOrId) throws UsernameNotFoundException {
//        // Attempt to find the user by username, email, or OAuth2 ID
//        User user = userRepository.findByUsername(usernameOrEmailOrId)
//                .or(() -> userRepository.findByEmail(usernameOrEmailOrId))  // Try finding by email
//                .or(() -> userRepository.findByOauth2Id(usernameOrEmailOrId))  // Try finding by OAuth2 ID
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + usernameOrEmailOrId));
//
//        // Fetch roles and convert them to GrantedAuthority
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
//
//        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
//    }
//
//
//}



package com.ncnmo.aspire.elearning.service;

import com.ncnmo.aspire.elearning.model.User;
import com.ncnmo.aspire.elearning.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmailOrId) throws UsernameNotFoundException {
        // Attempt to find the user by username, email, or OAuth2 ID
        User user = userRepository.findByUsername(usernameOrEmailOrId)
                .or(() -> userRepository.findByEmail(usernameOrEmailOrId))  // Try finding by email
                .or(() -> userRepository.findByOauth2Id(usernameOrEmailOrId))  // Try finding by OAuth2 ID
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + usernameOrEmailOrId));

        // Fetch roles and convert them to GrantedAuthority
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));

        // For OAuth2 users, the password can be a placeholder as they don't use passwords
        String password = user.isOAuth2User() ? "{noop}" : user.getPassword();

        return new org.springframework.security.core.userdetails.User(user.getUsername(), password, authorities);
    }

}

