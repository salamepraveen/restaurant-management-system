package com.prav.order;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import jakarta.validation.*;

import com.prav.order.dto.OrderRequestDTO;
import com.prav.order.dto.OrderRequestDTO.OrderItemDTO;
import com.prav.order.dto.OrderStatusDTO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class DTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validOrderRequest_noErrors() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(1L);

        OrderItemDTO item = new OrderItemDTO();
        item.setPizzaId(1L);
        item.setQuantity(2);
        dto.setItems(List.of(item));

        Set<ConstraintViolation<OrderRequestDTO>> errors = validator.validate(dto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void orderRequest_nullRestaurantId() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(null);

        OrderItemDTO item = new OrderItemDTO();
        item.setPizzaId(1L);
        item.setQuantity(1);
        dto.setItems(List.of(item));

        Set<ConstraintViolation<OrderRequestDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Restaurant ID")));
    }

    @Test
    void orderRequest_nullItems() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(1L);
        dto.setItems(null);

        Set<ConstraintViolation<OrderRequestDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
    }

    @Test
    void orderRequest_emptyItems() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(1L);
        dto.setItems(List.of());

        Set<ConstraintViolation<OrderRequestDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("At least one")));
    }

    @Test
    void orderItem_nullPizzaId() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(1L);

        OrderItemDTO item = new OrderItemDTO();
        item.setPizzaId(null);
        item.setQuantity(1);
        dto.setItems(List.of(item));

        Set<ConstraintViolation<OrderRequestDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Pizza ID")));
    }

    @Test
    void orderItem_nullQuantity() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(1L);

        OrderItemDTO item = new OrderItemDTO();
        item.setPizzaId(1L);
        item.setQuantity(null);
        dto.setItems(List.of(item));

        Set<ConstraintViolation<OrderRequestDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Quantity")));
    }

    @Test
    void orderItem_quantityZero() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(1L);

        OrderItemDTO item = new OrderItemDTO();
        item.setPizzaId(1L);
        item.setQuantity(0);
        dto.setItems(List.of(item));

        Set<ConstraintViolation<OrderRequestDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("at least 1")));
    }

    @Test
    void orderItem_quantityTooHigh() {
        OrderRequestDTO dto = new OrderRequestDTO();
        dto.setRestaurantId(1L);

        OrderItemDTO item = new OrderItemDTO();
        item.setPizzaId(1L);
        item.setQuantity(25);
        dto.setItems(List.of(item));

        Set<ConstraintViolation<OrderRequestDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("Maximum 20")));
    }

    @Test
    void validStatus_noErrors() {
        OrderStatusDTO dto = new OrderStatusDTO();
        dto.setStatus("CONFIRMED");

        Set<ConstraintViolation<OrderStatusDTO>> errors = validator.validate(dto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void status_allValidValues() {
        String[] validStatuses = {"PLACED", "CONFIRMED", "PREPARING", "READY", "DELIVERED", "CANCELLED"};

        for (String status : validStatuses) {
            OrderStatusDTO dto = new OrderStatusDTO();
            dto.setStatus(status);
            Set<ConstraintViolation<OrderStatusDTO>> errors = validator.validate(dto);
            assertTrue(errors.isEmpty(), "Status " + status + " should be valid");
        }
    }

    @Test
    void status_blank() {
        OrderStatusDTO dto = new OrderStatusDTO();
        dto.setStatus("");

        Set<ConstraintViolation<OrderStatusDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
    }

    @Test
    void status_null() {
        OrderStatusDTO dto = new OrderStatusDTO();
        dto.setStatus(null);

        Set<ConstraintViolation<OrderStatusDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
    }

    @Test
    void status_invalidValue() {
        OrderStatusDTO dto = new OrderStatusDTO();
        dto.setStatus("SHIPPED");

        Set<ConstraintViolation<OrderStatusDTO>> errors = validator.validate(dto);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("one of")));
    }
}