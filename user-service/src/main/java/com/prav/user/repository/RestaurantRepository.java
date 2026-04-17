package com.prav.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.prav.user.model.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
	
	
}