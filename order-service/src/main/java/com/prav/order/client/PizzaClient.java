package com.prav.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@FeignClient(name = "PIZZA-SERVICE")
public interface PizzaClient {

	@GetMapping("/pizzas/internal/{id}")
    Map<String, Object> getPizzaById(@PathVariable Long id);

    @GetMapping("/pizzas/toppings")
    Map<String, Object> getAllToppings();
}