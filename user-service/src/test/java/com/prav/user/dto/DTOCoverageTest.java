package com.prav.user.dto;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class DTOCoverageTest {

    @Test
    void testUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("prav");
        dto.setPassword("password");
        dto.setEmail("prav@test.com");
        dto.setRole("ADMIN");
        dto.setAssignedRestaurantId(10L);
        dto.setKnownRestaurantIds(Collections.singletonList(10L));
        dto.setPhoneNumber("1234567890");
        dto.setAddress("Test Address");

        assertEquals(1L, dto.getId());
        assertEquals("prav", dto.getUsername());
        assertEquals("password", dto.getPassword());
        assertEquals("prav@test.com", dto.getEmail());
        assertEquals("ADMIN", dto.getRole());
        assertEquals(10L, dto.getAssignedRestaurantId());
        assertEquals(1, dto.getKnownRestaurantIds().size());
        assertEquals("1234567890", dto.getPhoneNumber());
        assertEquals("Test Address", dto.getAddress());
    }

    @Test
    void testPromoteRequestDTO() {
        PromoteRequestDTO dto = new PromoteRequestDTO();
        dto.setRole("STAFF");
        assertEquals("STAFF", dto.getRole());
    }

    @Test
    void testRestaurantCreateRequestDTO() {
        RestaurantCreateRequestDTO dto = new RestaurantCreateRequestDTO();
        dto.setName("Pizza Palace");
        dto.setCity("New York");
        dto.setAddress("123 Broadway");

        assertEquals("Pizza Palace", dto.getName());
        assertEquals("New York", dto.getCity());
        assertEquals("123 Broadway", dto.getAddress());
    }

    @Test
    void testPizzaSizeRequestDTO() {
        PizzaSizeRequestDTO dto = new PizzaSizeRequestDTO();
        dto.setSize("LARGE");
        dto.setPrice(15.99);

        assertEquals("LARGE", dto.getSize());
        assertEquals(15.99, dto.getPrice());
    }
}
