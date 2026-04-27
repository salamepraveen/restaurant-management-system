package com.prav.pizza.model;
 
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
 
class ModelCoverageTest {
 
    @Test
    void testPizza() {
        Pizza pizza = new Pizza();
        pizza.setId(1L);
        pizza.setName("n");
        pizza.setDescription("d");
        pizza.setBasePrice(10.0);
        pizza.setRestaurantId(2L);
        pizza.setVegetarian(true);
        pizza.setImageUrl("i");
        pizza.setSizes(new ArrayList<>());
        pizza.setToppings(new ArrayList<>());
 
        assertEquals(1L, pizza.getId());
        assertEquals("n", pizza.getName());
        assertEquals("d", pizza.getDescription());
        assertEquals(10.0, pizza.getBasePrice());
        assertEquals(2L, pizza.getRestaurantId());
        assertTrue(pizza.getVegetarian());
        assertEquals("i", pizza.getImageUrl());
        assertNotNull(pizza.getSizes());
        assertNotNull(pizza.getToppings());
    }
 
    @Test
    void testPizzaSize() {
        PizzaSize size = new PizzaSize();
        size.setId(1L);
        size.setSize("s");
        size.setPrice(5.0);
        size.setPizza(new Pizza());
 
        assertEquals(1L, size.getId());
        assertEquals("s", size.getSize());
        assertEquals(5.0, size.getPrice());
        assertNotNull(size.getPizza());
    }
 
    @Test
    void testTopping() {
        Topping topping = new Topping();
        topping.setId(1L);
        topping.setName("t");
        topping.setPrice(1.0);
        topping.setRestaurantId(2L);
        topping.setIsAvailable(true);
        topping.setPizzas(new ArrayList<>());
 
        assertEquals(1L, topping.getId());
        assertEquals("t", topping.getName());
        assertEquals(1.0, topping.getPrice());
        assertEquals(2L, topping.getRestaurantId());
        assertTrue(topping.getIsAvailable());
        assertNotNull(topping.getPizzas());
    }
}
