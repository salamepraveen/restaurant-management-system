package com.prav.pizza;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

import com.prav.pizza.dto.*;

public class DTOCoverageTest {

    @Test
    void testPizzaDTO() {
        PizzaDTO dto = new PizzaDTO();
        dto.setId(1L);
        dto.setName("Margherita");
        dto.setDescription("Classic");
        dto.setImageUrl("url");
        dto.setVegetarian(true);
        dto.setRestaurantId(100L);
        dto.setBasePrice(10.0);
        dto.setIsAvailable(true);
        
        List<PizzaSizeDTO> sizes = new ArrayList<>();
        dto.setSizes(sizes);
        
        List<ToppingDTO> toppings = new ArrayList<>();
        dto.setToppings(toppings);
        
        assertEquals(1L, dto.getId());
        assertEquals("Margherita", dto.getName());
        assertEquals("Classic", dto.getDescription());
        assertEquals("url", dto.getImageUrl());
        assertTrue(dto.getVegetarian());
        assertEquals(100L, dto.getRestaurantId());
        assertEquals(10.0, dto.getBasePrice());
        assertTrue(dto.getIsAvailable());
        assertSame(sizes, dto.getSizes());
        assertSame(toppings, dto.getToppings());
    }

    @Test
    void testPizzaCreateRequestDTO() {
        PizzaCreateRequestDTO dto = new PizzaCreateRequestDTO();
        dto.setName("Veggie");
        dto.setDescription("Healthy");
        dto.setVegetarian(true);
        dto.setBasePrice(12.0);
        
        assertEquals("Veggie", dto.getName());
        assertEquals("Healthy", dto.getDescription());
        assertTrue(dto.getVegetarian());
        assertEquals(12.0, dto.getBasePrice());
    }

    @Test
    void testPizzaSizeDTO() {
        PizzaSizeDTO dto = new PizzaSizeDTO();
        dto.setId(1L);
        dto.setSize("MEDIUM");
        dto.setPrice(15.0);
        
        assertEquals(1L, dto.getId());
        assertEquals("MEDIUM", dto.getSize());
        assertEquals(15.0, dto.getPrice());
    }

    @Test
    void testPizzaSizeRequestDTO() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        assertNotNull(dto);
    }

    @Test
    void testToppingDTO() {
        ToppingDTO dto = new ToppingDTO();
        dto.setId(1L);
        dto.setName("Cheese");
        dto.setPrice(2.0);
        dto.setIsAvailable(true);
        
        assertEquals(1L, dto.getId());
        assertEquals("Cheese", dto.getName());
        assertEquals(2.0, dto.getPrice());
        assertTrue(dto.getIsAvailable());
    }

    @Test
    void testToppingCreateRequestDTO() {
        ToppingCreateRequestDTO dto = new ToppingCreateRequestDTO();
        dto.setName("Olives");
        dto.setPrice(1.5);
        dto.setVegetarian(true);
        
        assertEquals("Olives", dto.getName());
        assertEquals(1.5, dto.getPrice());
        assertTrue(dto.getVegetarian());
    }
}
