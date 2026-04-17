package com.prav.user.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ========== UserDTO ==========

    @Test
    void userDTO_allFieldsValid_noErrors() {
        UserDTO dto = new UserDTO();
        dto.setUsername("prav");
        dto.setPassword("pass123");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userDTO_nullUsername_returnsError() {
        UserDTO dto = new UserDTO();
        dto.setUsername(null);
        dto.setPassword("pass123");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void userDTO_blankUsername_returnsError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("   ");
        dto.setPassword("pass123");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void userDTO_usernameTooShort_returnsError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("p");
        dto.setPassword("pass123");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void userDTO_usernameTooLong_returnsError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("p".repeat(51));
        dto.setPassword("pass123");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void userDTO_usernameExactly2Chars_valid() {
        UserDTO dto = new UserDTO();
        dto.setUsername("pr");
        dto.setPassword("pass123");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userDTO_usernameExactly50Chars_valid() {
        UserDTO dto = new UserDTO();
        dto.setUsername("p".repeat(50));
        dto.setPassword("pass123");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userDTO_nullPassword_returnsError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("prav");
        dto.setPassword(null);

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void userDTO_blankPassword_returnsError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("prav");
        dto.setPassword("   ");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void userDTO_passwordTooShort_returnsError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("prav");
        dto.setPassword("12345");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void userDTO_passwordTooLong_returnsError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("prav");
        dto.setPassword("p".repeat(101));

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void userDTO_passwordExactly6Chars_valid() {
        UserDTO dto = new UserDTO();
        dto.setUsername("prav");
        dto.setPassword("123456");

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userDTO_nullRole_noError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("prav");
        dto.setPassword("pass123");
        dto.setRole(null);

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userDTO_nullAssignedRestaurantId_noError() {
        UserDTO dto = new UserDTO();
        dto.setUsername("prav");
        dto.setPassword("pass123");
        dto.setAssignedRestaurantId(null);

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    // ========== PromoteRequestDTO ==========

    @Test
    void promoteRequest_validStaff_noErrors() {
        PromoteRequestDTO dto = new PromoteRequestDTO();
        dto.setRole("STAFF");

        Set<ConstraintViolation<PromoteRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void promoteRequest_validAdmin_noErrors() {
        PromoteRequestDTO dto = new PromoteRequestDTO();
        dto.setRole("ADMIN");

        Set<ConstraintViolation<PromoteRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void promoteRequest_nullRole_returnsError() {
        PromoteRequestDTO dto = new PromoteRequestDTO();
        dto.setRole(null);

        Set<ConstraintViolation<PromoteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void promoteRequest_blankRole_returnsError() {
        PromoteRequestDTO dto = new PromoteRequestDTO();
        dto.setRole("   ");

        Set<ConstraintViolation<PromoteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void promoteRequest_invalidRole_returnsError() {
        PromoteRequestDTO dto = new PromoteRequestDTO();
        dto.setRole("SUPERADMIN");

        Set<ConstraintViolation<PromoteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("role")));
    }

    @Test
    void promoteRequest_userRole_returnsError() {
        PromoteRequestDTO dto = new PromoteRequestDTO();
        dto.setRole("USER");

        Set<ConstraintViolation<PromoteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void promoteRequest_lowercaseStaff_returnsError() {
        PromoteRequestDTO dto = new PromoteRequestDTO();
        dto.setRole("staff");

        Set<ConstraintViolation<PromoteRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    // ========== PizzaSizeRequestDTO ==========

    @Test
    void pizzaSizeRequest_validMedium_noErrors() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        dto.setSize("MEDIUM");
        dto.setPrice(399.0);

        Set<ConstraintViolation<PizzaSizeRequestDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void pizzaSizeRequest_allValidSizes_noErrors() {
        for (String size : List.of("SMALL", "MEDIUM", "LARGE", "EXTRA_LARGE")) {
            PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
            dto.setSize(size);
            dto.setPrice(100.0);

            Set<ConstraintViolation<PizzaSizeRequestDTO>> violations = validator.validate(dto);
            assertTrue(violations.isEmpty(), "Failed for size: " + size);
        }
    }

    @Test
    void pizzaSizeRequest_nullSize_returnsError() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        dto.setSize(null);
        dto.setPrice(399.0);

        Set<ConstraintViolation<PizzaSizeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("size")));
    }

    @Test
    void pizzaSizeRequest_blankSize_returnsError() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        dto.setSize("   ");
        dto.setPrice(399.0);

        Set<ConstraintViolation<PizzaSizeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void pizzaSizeRequest_invalidSize_returnsError() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        dto.setSize("GIGANTIC");
        dto.setPrice(399.0);

        Set<ConstraintViolation<PizzaSizeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void pizzaSizeRequest_nullPrice_returnsError() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        dto.setSize("MEDIUM");
        dto.setPrice(null);

        Set<ConstraintViolation<PizzaSizeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    void pizzaSizeRequest_negativePrice_returnsError() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        dto.setSize("MEDIUM");
        dto.setPrice(-50.0);

        Set<ConstraintViolation<PizzaSizeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void pizzaSizeRequest_zeroPrice_returnsError() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        dto.setSize("MEDIUM");
        dto.setPrice(0.0);

        Set<ConstraintViolation<PizzaSizeRequestDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}