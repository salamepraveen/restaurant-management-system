package com.prav.pizza.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ToppingDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void allFieldsValid_noErrors() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("Olives");
        dto.setPrice(50.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullName_returnsError() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName(null);
        dto.setPrice(50.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void blankName_returnsError() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("   ");
        dto.setPrice(50.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nameTooShort_returnsError() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("O");
        dto.setPrice(50.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void nameTooLong_returnsError() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("O".repeat(51));
        dto.setPrice(50.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void nameExactly2Chars_valid() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("Ol");
        dto.setPrice(50.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nameExactly50Chars_valid() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("O".repeat(50));
        dto.setPrice(50.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullPrice_returnsError() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("Olives");
        dto.setPrice(null);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    void negativePrice_returnsError() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("Olives");
        dto.setPrice(-10.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void zeroPrice_returnsError() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("Olives");
        dto.setPrice(0.0);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void nullId_noError() {
        ToppingDTO dto = new ToppingDTO();
        dto.setName("Olives");
        dto.setPrice(50.0);
        dto.setId(null);

        Set<ConstraintViolation<ToppingDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}