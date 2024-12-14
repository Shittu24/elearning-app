package com.ncnmo.aspire.elearning.repository;

import com.ncnmo.aspire.elearning.model.Role;
import com.ncnmo.aspire.elearning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByOauth2Id(String oauth2Id);
}
