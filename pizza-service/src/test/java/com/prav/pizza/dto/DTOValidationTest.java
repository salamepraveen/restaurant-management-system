package com.prav.pizza.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ========== PizzaCreateRequestDTO ==========

    @Test
    void pizzaCreate_valid() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setName("Margherita");
        dto.setBasePrice(299.0);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void pizzaCreate_nullName() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setBasePrice(299.0);
        Set<ConstraintViolation<PizzaCreateRequestDTO>> v = validator.validate(dto);
        assertTrue(v.stream().anyMatch(e -> e.getPropertyPath().toString().equals("name")));
    }

    @Test
    void pizzaCreate_blankName() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setName("   ");
        dto.setBasePrice(299.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void pizzaCreate_nameTooShort() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setName("p");
        dto.setBasePrice(299.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void pizzaCreate_nameTooLong() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setName("p".repeat(101));
        dto.setBasePrice(299.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void pizzaCreate_nullBasePrice() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setName("Margherita");
        Set<ConstraintViolation<PizzaCreateRequestDTO>> v = validator.validate(dto);
        assertTrue(v.stream().anyMatch(e -> e.getPropertyPath().toString().equals("basePrice")));
    }

    @Test
    void pizzaCreate_negativePrice() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setName("Margherita");
        dto.setBasePrice(-10.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void pizzaCreate_zeroPrice() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setName("Margherita");
        dto.setBasePrice(0.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    // ========== ToppingCreateRequestDTO ==========

    @Test
    void toppingCreate_valid() {
        ToppingCreateRequestDTO dto = new ToppingCreateRequestDTO();
        dto.setName("Olives");
        dto.setPrice(50.0);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void toppingCreate_nullName() {
        ToppingCreateRequestDTO dto = new ToppingCreateRequestDTO();
        dto.setPrice(50.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void toppingCreate_blankName() {
        ToppingCreateRequestDTO dto = new ToppingCreateRequestDTO();
        dto.setName("   ");
        dto.setPrice(50.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void toppingCreate_nameTooShort() {
        ToppingCreateRequestDTO dto = new ToppingCreateRequestDTO();
        dto.setName("O");
        dto.setPrice(50.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void toppingCreate_nameTooLong() {
        ToppingCreateRequestDTO dto = new ToppingCreateRequestDTO();
        dto.setName("O".repeat(51));
        dto.setPrice(50.0);
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void toppingCreate_nullPrice() {
        ToppingCreateRequestDTO dto = new ToppingCreateRequestDTO();
        dto.setName("Olives");
        Set<ConstraintViolation<ToppingCreateRequestDTO>> v = validator.validate(dto);
        assertTrue(v.stream().anyMatch(e -> e.getPropertyPath().toString().equals("price")));
    }

    @Test
    void toppingCreate_negativePrice() {
        ToppingCreateRequestDTO dto = new ToppingCreateRequestDTO();
        dto.setName("Olives");
        dto.setPrice(-5.0);
        assertFalse(validator.validate(dto).isEmpty());
    }
}