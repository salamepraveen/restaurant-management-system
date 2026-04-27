package com.prav.user.model;
 
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
 
class ModelCoverageTest {
 
    @Test
    void testUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("u");
        user.setPassword("p");
        user.setEmail("e");
        user.setRole("r");
        user.setAssignedRestaurantId(2L);
        user.setKnownRestaurantIds(new ArrayList<>());
        user.setPhoneNumber("123");
        user.setAddress("addr");
 
        assertEquals(1L, user.getId());
        assertEquals("u", user.getUsername());
        assertEquals("p", user.getPassword());
        assertEquals("e", user.getEmail());
        assertEquals("r", user.getRole());
        assertEquals(2L, user.getAssignedRestaurantId());
        assertNotNull(user.getKnownRestaurantIds());
        assertEquals("123", user.getPhoneNumber());
        assertEquals("addr", user.getAddress());
    }
 
    @Test
    void testRestaurant() {
        Restaurant res = new Restaurant();
        res.setId(1L);
        res.setName("n");
        res.setCity("c");
        res.setAddress("a");
        res.setOwnerId(2L);
 
        assertEquals(1L, res.getId());
        assertEquals("n", res.getName());
        assertEquals("c", res.getCity());
        assertEquals("a", res.getAddress());
        assertEquals(2L, res.getOwnerId());
    }
}
