package com.ncnmo.aspire.elearning.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ValidationController {

    @PostMapping("/api/test-email")
    public ResponseEntity<?> testEmailValidation(@RequestParam String email) {
        // Create a custom object only for email validation
        class EmailValidation {
            @NotBlank(message = "Email is required")
            @Email(message = "Email should be valid")
            private String email;

            public EmailValidation(String email) {
                this.email = email;
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }
        }

        EmailValidation emailValidation = new EmailValidation(email);

        // Perform validation on the email field only
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<EmailValidation>> violations = validator.validate(emailValidation);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<EmailValidation> violation : violations) {
                sb.append(violation.getMessage()).append(" ");
            }
            return ResponseEntity.badRequest().body(sb.toString().trim());
        }

        return ResponseEntity.ok("Email is valid");
    }
}
