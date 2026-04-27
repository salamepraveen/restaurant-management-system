package com.prav.pizza.service;

import com.prav.pizza.dto.*;
import com.prav.pizza.model.*;
import com.prav.pizza.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PizzaServiceTest {

    @Mock private PizzaRepository pizzaRepo;
    @Mock private PizzaSizeRepository sizeRepo;
    @Mock private ToppingRepository toppingRepo;
    private PizzaService service;

    private Pizza pizza;
    private Topping topping;
    private Map<String, Object> body;

    @BeforeEach
    void setUp() {
        service = new PizzaService(pizzaRepo, sizeRepo, toppingRepo);
        pizza = new Pizza();
        pizza.setId(1L);
        pizza.setName("Margherita");
        pizza.setDescription("Classic cheese");
        pizza.setImageUrl("img.png");
        pizza.setVegetarian(true);
        pizza.setBasePrice(299.0);
        pizza.setRestaurantId(100L);
        pizza.setSizes(new ArrayList<>());
        pizza.setToppings(new ArrayList<>());

        topping = new Topping();
        topping.setId(10L);
        topping.setName("Olives");
        topping.setPrice(50.0);
        topping.setRestaurantId(100L);

        body = new HashMap<>();
        body.put("name", "Margherita");
        body.put("description", "Classic cheese");
        body.put("imageUrl", "img.png");
        body.put("vegetarian", true);
        body.put("price", 299.0);
    }

    // ========== createPizza ==========

    @Test
    void createPizza_basic_savesAndReturns() {
        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(i -> {
            Pizza p = i.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        PizzaDTO result = service.createPizza(100L, body);

        assertEquals("Margherita", result.getName());
        assertEquals(299.0, result.getBasePrice());
        assertTrue(result.getVegetarian());
        verify(pizzaRepo, atLeastOnce()).save(any());
    }

    @Test
    void createPizza_withSizes_addsSizes() {
        List<Map<String, Object>> sizes = new ArrayList<>();
        Map<String, Object> s1 = new HashMap<>();
        s1.put("size", "MEDIUM");
        s1.put("price", 399.0);
        sizes.add(s1);
        body.put("sizes", sizes);

        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(i -> {
            Pizza p = i.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        PizzaDTO result = service.createPizza(100L, body);

        verify(pizzaRepo, atLeastOnce()).save(any());
    }

    @Test
    void createPizza_withToppingIds_addsToppings() {
        body.put("toppingIds", List.of(10L));
        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(i -> {
            Pizza p = i.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));
        when(toppingRepo.findAllById(List.of(10L))).thenReturn(List.of(topping));

        PizzaDTO result = service.createPizza(100L, body);

        assertNotNull(result.getToppings());
        verify(toppingRepo).findAllById(List.of(10L));
    }

    @Test
    void createPizza_withoutVegetarian_defaultsFalse() {
        body.remove("vegetarian");
        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(i -> {
            Pizza p = i.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        service.createPizza(100L, body);

        verify(pizzaRepo).save(argThat(p -> !p.getVegetarian()));
    }

    @Test
    void createPizza_withoutPrice_defaultsZero() {
        body.remove("price");
        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(i -> {
            Pizza p = i.getArgument(0);
            p.setId(1L);
            return p;
        });
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        service.createPizza(100L, body);

        verify(pizzaRepo).save(argThat(p -> p.getBasePrice() == 0.0));
    }

    // ========== updatePizza ==========

    @Test
    void updatePizza_success() {
        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("name", "Updated Margherita");

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));
        when(pizzaRepo.save(any(Pizza.class))).thenAnswer(i -> i.getArgument(0));

        PizzaDTO result = service.updatePizza(1L, 100L, updateBody);

        assertEquals("Updated Margherita", result.getName());
    }

    @Test
    void updatePizza_notFound_throwsException() {
        when(pizzaRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.updatePizza(999L, 100L, body));
    }

    @Test
    void updatePizza_wrongRestaurant_throwsException() {
        pizza.setRestaurantId(200L);
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        assertThrows(RuntimeException.class, () -> service.updatePizza(1L, 100L, body));
    }

    @Test
    void updatePizza_updatesMultipleFields() {
        Map<String, Object> updateBody = new HashMap<>();
        updateBody.put("name", "New Name");
        updateBody.put("description", "New Desc");
        updateBody.put("imageUrl", "new.png");
        updateBody.put("vegetarian", false);

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));
        when(pizzaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        PizzaDTO result = service.updatePizza(1L, 100L, updateBody);

        assertEquals("New Name", result.getName());
        assertEquals("New Desc", result.getDescription());
        assertEquals("new.png", result.getImageUrl());
        assertFalse(result.getVegetarian());
    }

    // ========== deletePizza ==========

    @Test
    void deletePizza_success() {
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        service.deletePizza(1L, 100L);

        verify(pizzaRepo).delete(pizza);
    }

    @Test
    void deletePizza_notFound_throwsException() {
        when(pizzaRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deletePizza(999L, 100L));
    }

    @Test
    void deletePizza_wrongRestaurant_throwsException() {
        pizza.setRestaurantId(200L);
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        assertThrows(RuntimeException.class, () -> service.deletePizza(1L, 100L));
    }

    // ========== Public read methods ==========

    @Test
    void getAllPizzas_returnsList() {
        when(pizzaRepo.findAll()).thenReturn(List.of(pizza));

        List<PizzaDTO> result = service.getAllPizzas();

        assertEquals(1, result.size());
        assertEquals("Margherita", result.get(0).getName());
    }

    @Test
    void getAllPizzas_empty() {
        when(pizzaRepo.findAll()).thenReturn(Collections.emptyList());

        List<PizzaDTO> result = service.getAllPizzas();

        assertTrue(result.isEmpty());
    }

    @Test
    void getPizzasByRestaurant() {
        when(pizzaRepo.findByRestaurantId(100L)).thenReturn(List.of(pizza));

        List<PizzaDTO> result = service.getPizzasByRestaurant(100L);

        assertEquals(1, result.size());
        verify(pizzaRepo).findByRestaurantId(100L);
    }

    @Test
    void getPizzaById() {
        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        PizzaDTO result = service.getPizzaById(1L);

        assertEquals("Margherita", result.getName());
    }

    @Test
    void getPizzaById_notFound() {
        when(pizzaRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getPizzaById(999L));
    }

    @Test
    void searchPizzas() {
        when(pizzaRepo.findByNameContainingIgnoreCase("marg")).thenReturn(List.of(pizza));

        List<PizzaDTO> result = service.searchPizzas("marg");

        assertEquals(1, result.size());
    }

    @Test
    void searchPizzas_noResults() {
        when(pizzaRepo.findByNameContainingIgnoreCase("xyz")).thenReturn(Collections.emptyList());

        List<PizzaDTO> result = service.searchPizzas("xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    void getVegetarianPizzas() {
        when(pizzaRepo.findByVegetarianTrue()).thenReturn(List.of(pizza));

        List<PizzaDTO> result = service.getVegetarianPizzas();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getVegetarian());
    }

    // ========== Size management ==========

    @Test
    void addSize_success() {
        Map<String, Object> sizeBody = new HashMap<>();
        sizeBody.put("size", "LARGE");
        sizeBody.put("price", 499.0);

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));
        when(pizzaRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        PizzaSizeDTO result = service.addSize(1L, 100L, sizeBody);

        assertEquals("LARGE", result.getSize());
        assertEquals(499.0, result.getPrice());
    }

    @Test
    void addSize_pizzaNotFound_throwsException() {
        Map<String, Object> sizeBody = new HashMap<>();
        sizeBody.put("size", "LARGE");
        sizeBody.put("price", 499.0);

        when(pizzaRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.addSize(999L, 100L, sizeBody));
    }

    @Test
    void addSize_wrongRestaurant_throwsException() {
        pizza.setRestaurantId(200L);
        Map<String, Object> sizeBody = new HashMap<>();
        sizeBody.put("size", "LARGE");
        sizeBody.put("price", 499.0);

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        assertThrows(RuntimeException.class, () -> service.addSize(1L, 100L, sizeBody));
    }

    @Test
    void deleteSize_success() {
        PizzaSize size = new PizzaSize();
        size.setId(5L);
        size.setSize("MEDIUM");
        size.setPrice(399.0);
        size.setPizza(pizza);

        when(sizeRepo.findById(5L)).thenReturn(Optional.of(size));

        service.deleteSize(5L, 100L);

        verify(sizeRepo).delete(size);
    }

    @Test
    void deleteSize_notFound_throwsException() {
        when(sizeRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteSize(999L, 100L));
    }

    @Test
    void deleteSize_wrongRestaurant_throwsException() {
        pizza.setRestaurantId(200L);
        PizzaSize size = new PizzaSize();
        size.setPizza(pizza);

        when(sizeRepo.findById(5L)).thenReturn(Optional.of(size));

        assertThrows(RuntimeException.class, () -> service.deleteSize(5L, 100L));
    }

    // ========== Topping CRUD ==========

    @Test
    void createTopping_success() {
        Map<String, Object> toppingBody = new HashMap<>();
        toppingBody.put("name", "Cheese");
        toppingBody.put("price", 40.0);

        when(toppingRepo.save(any(Topping.class))).thenAnswer(i -> {
            Topping t = i.getArgument(0);
            t.setId(10L);
            return t;
        });

        ToppingDTO result = service.createTopping(100L, toppingBody);

        assertEquals("Cheese", result.getName());
        assertEquals(40.0, result.getPrice());
    }

    @Test
    void getAllToppings() {
        when(toppingRepo.findAll()).thenReturn(List.of(topping));

        List<ToppingDTO> result = service.getAllToppings();

        assertEquals(1, result.size());
        assertEquals("Olives", result.get(0).getName());
    }

    @Test
    void getToppingsByRestaurant() {
        when(toppingRepo.findByRestaurantId(100L)).thenReturn(List.of(topping));

        List<ToppingDTO> result = service.getToppingsByRestaurant(100L);

        assertEquals(1, result.size());
    }

    @Test
    void deleteTopping_success() {
        when(toppingRepo.findById(10L)).thenReturn(Optional.of(topping));

        service.deleteTopping(10L, 100L);

        verify(toppingRepo).delete(topping);
    }

    @Test
    void deleteTopping_notFound_throwsException() {
        when(toppingRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteTopping(999L, 100L));
    }

    @Test
    void deleteTopping_wrongRestaurant_throwsException() {
        topping.setRestaurantId(200L);
        when(toppingRepo.findById(10L)).thenReturn(Optional.of(topping));

        assertThrows(RuntimeException.class, () -> service.deleteTopping(10L, 100L));
    }

    // ========== toDTO with sizes and toppings ==========

    @Test
    void toDTO_withSizesAndToppings_mapsAll() {
        PizzaSize size = new PizzaSize();
        size.setId(5L);
        size.setSize("MEDIUM");
        size.setPrice(399.0);
        size.setPizza(pizza);
        pizza.setSizes(List.of(size));
        pizza.setToppings(List.of(topping));

        when(pizzaRepo.findById(1L)).thenReturn(Optional.of(pizza));

        PizzaDTO result = service.getPizzaById(1L);

        assertEquals(1, result.getSizes().size());
        assertEquals("MEDIUM", result.getSizes().get(0).getSize());
        assertEquals(1, result.getToppings().size());
        assertEquals("Olives", result.getToppings().get(0).getName());
    }
}
