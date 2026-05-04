package com.prav.auth.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.prav.auth.dto.AuthRequest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AuthRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest_NoViolations() {
        AuthRequest req = new AuthRequest();
        req.setUsername("testuser");
        req.setPassword("Password@123");
        req.setEmail("test@example.com");

        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankUsername_OneViolation() {
        AuthRequest req = new AuthRequest();
        req.setUsername("");
        req.setPassword("Password@123");
        req.setEmail("test@example.com");

        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(req);
        assertEquals(1, violations.size());
        assertEquals("Username is required", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPassword_OneViolation() {
        AuthRequest req = new AuthRequest();
        req.setUsername("testuser");
        req.setPassword("12");
        req.setEmail("test@example.com");

        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(req);
        assertEquals(1, violations.size());
        assertEquals("Password must be at least 8 characters long, contain at least one digit, one lowercase letter, one uppercase letter, and one special character", violations.iterator().next().getMessage());
    }

    @Test
    void testAllInvalid_Violations() {
        AuthRequest req = new AuthRequest();
        req.setUsername("");
        req.setPassword("");
        req.setEmail("invalid-email");

        Set<ConstraintViolation<AuthRequest>> violations = validator.validate(req);
        assertEquals(4, violations.size()); // 1 for username blank, 2 for password (NotBlank + Pattern), 1 for email Pattern
    }
}