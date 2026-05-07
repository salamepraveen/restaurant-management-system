package com.prav.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.prav.order.dto.UserDTO;

@FeignClient(name = "USER-SERVICE", url = "http://localhost:8081")
public interface UserClient {

    @GetMapping("/users/internal/{id}")
    UserDTO getUserById(@PathVariable Long id);

    @GetMapping("/users/internal/restaurant/{id}")
    com.prav.order.dto.RestaurantDTO getRestaurantById(@PathVariable("id") Long id);
}