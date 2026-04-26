package com.prav.pizza;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

import com.prav.pizza.model.*;

public class ModelCoverageTest {

    @Test
    void testPizza() {
        Pizza pizza = new Pizza();
        pizza.setId(1L);
        pizza.setName("Margherita");
        pizza.setDescription("Classic");
        pizza.setImageUrl("url");
        pizza.setVegetarian(true);
        pizza.setRestaurantId(100L);
        pizza.setBasePrice(10.0);
        pizza.setIsAvailable(true);
        
        List<PizzaSize> sizes = new ArrayList<>();
        pizza.setSizes(sizes);
        
        List<Topping> toppings = new ArrayList<>();
        pizza.setToppings(toppings);
        
        assertEquals(1L, pizza.getId());
        assertEquals("Margherita", pizza.getName());
        assertEquals("Classic", pizza.getDescription());
        assertEquals("url", pizza.getImageUrl());
        assertTrue(pizza.getVegetarian());
        assertEquals(100L, pizza.getRestaurantId());
        assertEquals(10.0, pizza.getBasePrice());
        assertTrue(pizza.getIsAvailable());
        assertSame(sizes, pizza.getSizes());
        assertSame(toppings, pizza.getToppings());
    }

    @Test
    void testPizzaSize() {
        PizzaSize size = new PizzaSize();
        size.setId(1L);
        size.setSize("LARGE");
        size.setPrice(20.0);
        
        Pizza pizza = new Pizza();
        size.setPizza(pizza);
        
        assertEquals(1L, size.getId());
        assertEquals("LARGE", size.getSize());
        assertEquals(20.0, size.getPrice());
        assertSame(pizza, size.getPizza());
    }

    @Test
    void testTopping() {
        Topping topping = new Topping();
        topping.setId(1L);
        topping.setName("Mushrooms");
        topping.setPrice(3.0);
        topping.setRestaurantId(100L);
        topping.setIsAvailable(true);
        
        assertEquals(1L, topping.getId());
        assertEquals("Mushrooms", topping.getName());
        assertEquals(3.0, topping.getPrice());
        assertEquals(100L, topping.getRestaurantId());
        assertTrue(topping.getIsAvailable());
    }
}
