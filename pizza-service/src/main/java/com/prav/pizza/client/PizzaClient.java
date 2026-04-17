package com.prav.pizza.client;

import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PIZZA-SERVICE")
public interface PizzaClient {

	@GetMapping("/pizzas/internal/{id}")
    Map<String, Object> getPizzaById(@PathVariable Long id);
}