package com.prav.pizza.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prav.pizza.model.Topping;

public interface ToppingRepository extends JpaRepository<Topping, Long> {
    List<Topping> findByRestaurantId(Long restaurantId);
    List<Topping> findByNameContainingIgnoreCase(String keyword);
}