package com.prav.pizza.controller;

import com.prav.pizza.dto.*;
import com.prav.pizza.service.PizzaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PizzaController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class
})
class PizzaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private PizzaService service;

    private Map<String, Object> pizzaBody() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Margherita");
        body.put("description", "Classic cheese");
        body.put("price", 299.0);
        body.put("vegetarian", true);
        return body;
    }

    // ========== ADMIN - Create ==========

    @Test
    void createPizza_asAdmin_returns201() throws Exception {
        when(service.createPizza(anyLong(), anyMap())).thenReturn(new PizzaDTO());

        mockMvc.perform(post("/pizzas")
                        .header("X-User-Id", 1L)
                        .header("X-User-Role", "ADMIN")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pizzaBody())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Pizza created successfully"));
    }

    @Test
    void createPizza_asUser_returns403() throws Exception {
        mockMvc.perform(post("/pizzas")
                        .header("X-User-Id", 2L)
                        .header("X-User-Role", "USER")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pizzaBody())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Only ADMIN can add pizzas"));
    }

    @Test
    void createPizza_asStaff_returns201() throws Exception {
        when(service.createPizza(anyLong(), anyMap())).thenReturn(new PizzaDTO());

        mockMvc.perform(post("/pizzas")
                        .header("X-User-Id", 3L)
                        .header("X-User-Role", "STAFF")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pizzaBody())))
                .andExpect(status().isCreated());
    }

    // ========== ADMIN - Update ==========

    @Test
    void updatePizza_asAdmin_returns200() throws Exception {
        when(service.updatePizza(anyLong(), anyLong(), anyMap())).thenReturn(new PizzaDTO());

        mockMvc.perform(put("/pizzas/1")
                        .header("X-User-Role", "ADMIN")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pizzaBody())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pizza updated successfully"));
    }

    @Test
    void updatePizza_asUser_returns403() throws Exception {
        mockMvc.perform(put("/pizzas/1")
                        .header("X-User-Role", "USER")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pizzaBody())))
                .andExpect(status().isForbidden());
    }

    // ========== ADMIN - Delete ==========

    @Test
    void deletePizza_asAdmin_returns200() throws Exception {
        doNothing().when(service).deletePizza(anyLong(), anyLong());

        mockMvc.perform(delete("/pizzas/1")
                        .header("X-User-Role", "ADMIN")
                        .header("X-Restaurant-Id", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pizza deleted successfully"));
    }

    @Test
    void deletePizza_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/pizzas/1")
                        .header("X-User-Role", "USER")
                        .header("X-Restaurant-Id", 100L))
                .andExpect(status().isForbidden());
    }

    // ========== PUBLIC - Browse ==========

    @Test
    void getAllPizzas_returns200() throws Exception {
        when(service.getAllPizzas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pizzas"))
                .andExpect(status().isOk());
    }

    @Test
    void getPizzaById_returns200() throws Exception {
        when(service.getPizzaById(1L)).thenReturn(new PizzaDTO());

        mockMvc.perform(get("/pizzas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    void getPizzaById_notFound_returns404() throws Exception {
        when(service.getPizzaById(999L)).thenThrow(new com.prav.pizza.exception.PizzaNotFoundException("Pizza not found"));

        mockMvc.perform(get("/pizzas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPizzasByRestaurant_returns200() throws Exception {
        when(service.getPizzasByRestaurant(100L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pizzas/restaurant/100"))
                .andExpect(status().isOk());
    }

    @Test
    void searchPizzas_returns200() throws Exception {
        when(service.searchPizzas(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pizzas/search").param("keyword", "Marg"))
                .andExpect(status().isOk());
    }

    @Test
    void getVegetarianPizzas_returns200() throws Exception {
        when(service.getVegetarianPizzas()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pizzas/vegetarian"))
                .andExpect(status().isOk());
    }

    // ========== ADMIN - Sizes ==========

    @Test
    void addSize_asAdmin_returns201() throws Exception {
        Map<String, Object> sizeBody = new HashMap<>();
        sizeBody.put("size", "LARGE");
        sizeBody.put("price", 499.0);

        when(service.addSize(anyLong(), anyLong(), anyMap())).thenReturn(new PizzaSizeDTO());

        mockMvc.perform(post("/pizzas/1/sizes")
                        .header("X-User-Role", "ADMIN")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sizeBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Size added successfully"));
    }

    @Test
    void addSize_asUser_returns403() throws Exception {
        Map<String, Object> sizeBody = new HashMap<>();
        sizeBody.put("size", "LARGE");
        sizeBody.put("price", 499.0);

        mockMvc.perform(post("/pizzas/1/sizes")
                        .header("X-User-Role", "USER")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sizeBody)))
                .andExpect(status().isForbidden());
    }

    // ========== ADMIN - Toppings ==========

    @Test
    void createTopping_asAdmin_returns201() throws Exception {
        Map<String, Object> toppingBody = new HashMap<>();
        toppingBody.put("name", "Cheese");
        toppingBody.put("price", 40.0);

        when(service.createTopping(anyLong(), anyMap())).thenReturn(new ToppingDTO());

        mockMvc.perform(post("/pizzas/toppings")
                        .header("X-User-Role", "ADMIN")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toppingBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Topping created successfully"));
    }

    @Test
    void createTopping_asUser_returns403() throws Exception {
        Map<String, Object> toppingBody = new HashMap<>();
        toppingBody.put("name", "Cheese");
        toppingBody.put("price", 40.0);

        mockMvc.perform(post("/pizzas/toppings")
                        .header("X-User-Role", "USER")
                        .header("X-Restaurant-Id", 100L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toppingBody)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllToppings_returns200() throws Exception {
        when(service.getAllToppings()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pizzas/toppings"))
                .andExpect(status().isOk());
    }

    @Test
    void getToppingsByRestaurant_returns200() throws Exception {
        when(service.getToppingsByRestaurant(100L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pizzas/toppings/restaurant/100"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTopping_asAdmin_returns200() throws Exception {
        doNothing().when(service).deleteTopping(anyLong(), anyLong());

        mockMvc.perform(delete("/pizzas/toppings/10")
                        .header("X-User-Role", "ADMIN")
                        .header("X-Restaurant-Id", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Topping deleted successfully"));
    }

    @Test
    void deleteTopping_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/pizzas/toppings/10")
                        .header("X-User-Role", "USER")
                        .header("X-Restaurant-Id", 100L))
                .andExpect(status().isForbidden());
    }
}