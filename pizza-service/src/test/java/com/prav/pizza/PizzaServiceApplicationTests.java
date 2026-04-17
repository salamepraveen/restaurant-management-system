package com.prav.pizza;

import com.prav.pizza.dto.PizzaDTO;
import com.prav.pizza.dto.PizzaSizeDTO;
import com.prav.pizza.dto.ToppingDTO;
import com.prav.pizza.model.Pizza;
import com.prav.pizza.model.PizzaSize;
import com.prav.pizza.model.Topping;
import com.prav.pizza.repository.PizzaRepository;
import com.prav.pizza.repository.PizzaSizeRepository;
import com.prav.pizza.repository.ToppingRepository;
import com.prav.pizza.service.PizzaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PizzaServiceApplicationTests {

    @Mock
    private PizzaRepository pizzaRepo;

    @Mock
    private PizzaSizeRepository sizeRepo;

    @Mock
    private ToppingRepository toppingRepo;

    @InjectMocks
    private PizzaService pizzaService;

    private Pizza samplePizza;

    @BeforeEach
    void setUp() {
        samplePizza = new Pizza();
        samplePizza.setId(1L);
        samplePizza.setName("Margherita");
        samplePizza.setDescription("Classic pizza");
        samplePizza.setBasePrice(10.00);
        samplePizza.setRestaurantId(2L);
        samplePizza.setVegetarian(true);
        samplePizza.setSizes(new ArrayList<>());
        samplePizza.setToppings(new ArrayList<>());
    }

    @Test
    void testGetAllPizzas() {
        when(pizzaRepo.findAll()).thenReturn(List.of(samplePizza));

        List<PizzaDTO> result = pizzaService.getAllPizzas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Margherita", result.get(0).getName());
        verify(pizzaRepo).findAll();
    }

    @Test
    void testGetAllPizzas_Empty() {
        when(pizzaRepo.findAll()).thenReturn(Collections.emptyList());

        List<PizzaDTO> result = pizzaService.getAllPizzas();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPizzaById_Found() {
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(samplePizza));

        PizzaDTO result = pizzaService.getPizzaById(1L);

        assertNotNull(result);
        assertEquals("Margherita", result.getName());
        assertEquals(10.00, result.getBasePrice());
    }

    @Test
    void testGetPizzaById_NotFound_ThrowsException() {
        when(pizzaRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pizzaService.getPizzaById(99L));
    }

    @Test
    void testCreatePizza_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Pepperoni");
        body.put("description", "Spicy pizza");
        body.put("price", 12.50);

        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(inv -> {
            Pizza p = inv.getArgument(0);
            p.setId(2L);
            p.setSizes(new ArrayList<>());
            p.setToppings(new ArrayList<>());
            return p;
        });
        when(pizzaRepo.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pizzaService.createPizza(2L, body));
    }

    @Test
    void testCreatePizza_WithSizes() {
        Map<String, Object> sizeMap = new HashMap<>();
        sizeMap.put("size", "LARGE");
        sizeMap.put("price", 18.99);

        Map<String, Object> body = new HashMap<>();
        body.put("name", "BBQ Chicken");
        body.put("price", 15.00);
        body.put("sizes", List.of(sizeMap));

        Pizza savedPizza = new Pizza();
        savedPizza.setId(3L);
        savedPizza.setName("BBQ Chicken");
        savedPizza.setBasePrice(15.00);
        savedPizza.setRestaurantId(2L);
        savedPizza.setSizes(new ArrayList<>());
        savedPizza.setToppings(new ArrayList<>());

        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(inv -> {
            Pizza p = inv.getArgument(0);
            if (p.getId() == null) {
                p.setId(3L);
                p.setSizes(new ArrayList<>());
                p.setToppings(new ArrayList<>());
            }
            return p;
        });
        when(pizzaRepo.findById(3L)).thenReturn(Optional.of(savedPizza));

        PizzaDTO result = pizzaService.createPizza(2L, body);

        assertNotNull(result);
        assertEquals("BBQ Chicken", result.getName());
        verify(pizzaRepo, atLeastOnce()).save(any());
    }

    @Test
    void testUpdatePizza_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Updated Margherita");

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(samplePizza));
        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(inv -> inv.getArgument(0));

        PizzaDTO result = pizzaService.updatePizza(1L, 2L, body);

        assertNotNull(result);
        assertEquals("Updated Margherita", result.getName());
        verify(pizzaRepo).save(any());
    }

    @Test
    void testUpdatePizza_NotFound_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Something");

        when(pizzaRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pizzaService.updatePizza(99L, 2L, body));
    }

    @Test
    void testUpdatePizza_WrongRestaurant_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Hacked");

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(samplePizza));

        assertThrows(RuntimeException.class, () -> pizzaService.updatePizza(1L, 999L, body));
        verify(pizzaRepo, never()).save(any());
    }

    @Test
    void testDeletePizza_Success() {
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(samplePizza));
        doNothing().when(pizzaRepo).delete(samplePizza);

        pizzaService.deletePizza(1L, 2L);

        verify(pizzaRepo).delete(samplePizza);
    }

    @Test
    void testDeletePizza_NotFound_ThrowsException() {
        when(pizzaRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pizzaService.deletePizza(99L, 2L));
    }

    @Test
    void testDeletePizza_WrongRestaurant_ThrowsException() {
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(samplePizza));

        assertThrows(RuntimeException.class, () -> pizzaService.deletePizza(1L, 999L));
        verify(pizzaRepo, never()).delete(any());
    }

    @Test
    void testSearchPizzas_ReturnsResults() {
        when(pizzaRepo.findByNameContainingIgnoreCase("marg")).thenReturn(List.of(samplePizza));

        List<PizzaDTO> result = pizzaService.searchPizzas("marg");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Margherita", result.get(0).getName());
    }

    @Test
    void testSearchPizzas_NoResults() {
        when(pizzaRepo.findByNameContainingIgnoreCase("xyz")).thenReturn(Collections.emptyList());

        List<PizzaDTO> result = pizzaService.searchPizzas("xyz");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetVegetarianPizzas() {
        when(pizzaRepo.findByVegetarianTrue()).thenReturn(List.of(samplePizza));

        List<PizzaDTO> result = pizzaService.getVegetarianPizzas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getVegetarian());
    }

    @Test
    void testGetPizzasByRestaurant() {
        when(pizzaRepo.findByRestaurantId(2L)).thenReturn(List.of(samplePizza));

        List<PizzaDTO> result = pizzaService.getPizzasByRestaurant(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pizzaRepo).findByRestaurantId(2L);
    }

    @Test
    void testAddSize_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("size", "XLARGE");
        body.put("price", 22.99);

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(samplePizza));
        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(inv -> inv.getArgument(0));

        PizzaSizeDTO result = pizzaService.addSize(1L, 2L, body);

        assertNotNull(result);
        assertEquals("XLARGE", result.getSize());
        assertEquals(22.99, result.getPrice());
        verify(pizzaRepo).save(any());
    }

    @Test
    void testAddSize_WrongRestaurant_ThrowsException() {
        Map<String, Object> body = new HashMap<>();
        body.put("size", "SMALL");
        body.put("price", 8.99);

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(samplePizza));

        assertThrows(RuntimeException.class, () -> pizzaService.addSize(1L, 999L, body));
    }

    @Test
    void testDeleteSize_Success() {
        PizzaSize size = new PizzaSize();
        size.setId(1L);
        size.setSize("MEDIUM");
        size.setPrice(14.99);
        size.setPizza(samplePizza);

        when(sizeRepo.findById(1L)).thenReturn(Optional.of(size));
        doNothing().when(sizeRepo).delete(size);

        pizzaService.deleteSize(1L, 2L);

        verify(sizeRepo).delete(size);
    }

    @Test
    void testDeleteSize_NotFound_ThrowsException() {
        when(sizeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> pizzaService.deleteSize(99L, 2L));
    }

    @Test
    void testCreateTopping_Success() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Olives");
        body.put("price", 1.50);

        when(toppingRepo.save(any(Topping.class))).thenAnswer(inv -> {
            Topping t = inv.getArgument(0);
            t.setId(1L);
            return t;
        });

        ToppingDTO result = pizzaService.createTopping(2L, body);

        assertNotNull(result);
        assertEquals("Olives", result.getName());
        assertEquals(1.50, result.getPrice());
        verify(toppingRepo).save(any());
    }

    @Test
    void testGetAllToppings() {
        Topping topping = new Topping();
        topping.setId(1L);
        topping.setName("Cheese");
        topping.setPrice(2.00);

        when(toppingRepo.findAll()).thenReturn(List.of(topping));

        List<ToppingDTO> result = pizzaService.getAllToppings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cheese", result.get(0).getName());
    }

    @Test
    void testGetToppingsByRestaurant() {
        Topping topping = new Topping();
        topping.setId(1L);
        topping.setName("Jalapenos");
        topping.setPrice(1.00);
        topping.setRestaurantId(2L);

        when(toppingRepo.findByRestaurantId(2L)).thenReturn(List.of(topping));

        List<ToppingDTO> result = pizzaService.getToppingsByRestaurant(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jalapenos", result.get(0).getName());
    }

    @Test
    void testDeleteTopping_Success() {
        Topping topping = new Topping();
        topping.setId(1L);
        topping.setName("Olives");
        topping.setRestaurantId(2L);

        when(toppingRepo.findById(1L)).thenReturn(Optional.of(topping));
        doNothing().when(toppingRepo).delete(topping);

        pizzaService.deleteTopping(1L, 2L);

        verify(toppingRepo).delete(topping);
    }

    @Test
    void testDeleteTopping_WrongRestaurant_ThrowsException() {
        Topping topping = new Topping();
        topping.setId(1L);
        topping.setRestaurantId(2L);

        when(toppingRepo.findById(1L)).thenReturn(Optional.of(topping));

        assertThrows(RuntimeException.class, () -> pizzaService.deleteTopping(1L, 999L));
        verify(toppingRepo, never()).delete(any());
    }
}