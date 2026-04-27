package com.prav.auth.model;
 
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
class ModelCoverageTest {
 
    @Test
    void testUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("u");
        user.setPassword("p");
        user.setRole("r");
        user.setRestaurantId(2L);
        user.setAssignedRestaurantId(3L);
 
        assertEquals(1L, user.getId());
        assertEquals("u", user.getUsername());
        assertEquals("p", user.getPassword());
        assertEquals("r", user.getRole());
        assertEquals(2L, user.getRestaurantId());
        assertEquals(3L, user.getAssignedRestaurantId());
    }
}
