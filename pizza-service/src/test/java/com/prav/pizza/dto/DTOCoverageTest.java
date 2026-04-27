package com.prav.pizza.dto;
 
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
 
class DTOCoverageTest {
 
    @Test
    void testPizzaDTO() {
        PizzaDTO dto = new PizzaDTO();
        dto.setId(1L);
        dto.setName("n");
        dto.setDescription("d");
        dto.setBasePrice(10.0);
        dto.setRestaurantId(2L);
        dto.setVegetarian(true);
        dto.setImageUrl("i");
        dto.setSizes(new ArrayList<>());
        dto.setToppings(new ArrayList<>());
 
        assertEquals(1L, dto.getId());
        assertEquals("n", dto.getName());
        assertEquals("d", dto.getDescription());
        assertEquals(10.0, dto.getBasePrice());
        assertEquals(2L, dto.getRestaurantId());
        assertTrue(dto.getVegetarian());
        assertEquals("i", dto.getImageUrl());
        assertNotNull(dto.getSizes());
        assertNotNull(dto.getToppings());
    }
 
    @Test
    void testPizzaSizeDTO() {
        PizzaSizeDTO dto = new PizzaSizeDTO();
        dto.setId(1L);
        dto.setSize("s");
        dto.setPrice(5.0);
 
        assertEquals(1L, dto.getId());
        assertEquals("s", dto.getSize());
        assertEquals(5.0, dto.getPrice());
    }
 
    @Test
    void testToppingDTO() {
        ToppingDTO dto = new ToppingDTO();
        dto.setId(1L);
        dto.setName("t");
        dto.setPrice(1.0);
        dto.setIsAvailable(true);
 
        assertEquals(1L, dto.getId());
        assertEquals("t", dto.getName());
        assertEquals(1.0, dto.getPrice());
        assertTrue(dto.getIsAvailable());
    }
}
