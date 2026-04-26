package com.prav.user.model;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ModelCoverageTest {

    @Test
    void testUserEntity() {
        User user = new User();
        user.setId(1L);
        user.setUsername("prav");
        user.setPassword("password");
        user.setEmail("prav@test.com");
        user.setRole("USER");
        user.setAssignedRestaurantId(10L);
        user.setPhoneNumber("1234567890");
        user.setAddress("Test Address");
        
        List<Long> ids = new ArrayList<>();
        ids.add(10L);
        user.setKnownRestaurantIds(ids);

        assertEquals(1L, user.getId());
        assertEquals("prav", user.getUsername());
        assertEquals("password", user.getPassword());
        assertEquals("prav@test.com", user.getEmail());
        assertEquals("USER", user.getRole());
        assertEquals(10L, user.getAssignedRestaurantId());
        assertEquals("1234567890", user.getPhoneNumber());
        assertEquals("Test Address", user.getAddress());
        assertEquals(1, user.getKnownRestaurantIds().size());
    }

    @Test
    void testRestaurantEntity() {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Pizza Palace");
        restaurant.setCity("New York");
        restaurant.setAddress("123 Broadway");
        restaurant.setOwnerId(100L);

        assertEquals(1L, restaurant.getId());
        assertEquals("Pizza Palace", restaurant.getName());
        assertEquals("New York", restaurant.getCity());
        assertEquals("123 Broadway", restaurant.getAddress());
        assertEquals(100L, restaurant.getOwnerId());
    }
}
