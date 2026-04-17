package com.prav.pizza.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prav.pizza.model.PizzaSize;


public interface PizzaSizeRepository extends JpaRepository<PizzaSize, Long> {
    List<PizzaSize> findByPizzaId(Long pizzaId);
}