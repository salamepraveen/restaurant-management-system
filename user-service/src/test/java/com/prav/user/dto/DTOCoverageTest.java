package com.prav.user.dto;
 
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
 
class DTOCoverageTest {
 
    @Test
    void testUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("u");
        dto.setEmail("e");
        dto.setPassword("p");
        dto.setRole("r");
        dto.setPhoneNumber("123");
        dto.setAddress("addr");
        dto.setAssignedRestaurantId(2L);
        dto.setKnownRestaurantIds(new ArrayList<>());
 
        assertEquals(1L, dto.getId());
        assertEquals("u", dto.getUsername());
        assertEquals("e", dto.getEmail());
        assertEquals("p", dto.getPassword());
        assertEquals("r", dto.getRole());
        assertEquals("123", dto.getPhoneNumber());
        assertEquals("addr", dto.getAddress());
        assertEquals(2L, dto.getAssignedRestaurantId());
        assertNotNull(dto.getKnownRestaurantIds());
    }

    @Test
    void testProfileUpdateRequestDTO() {
        ProfileUpdateRequestDTO dto = new ProfileUpdateRequestDTO();
        dto.setEmail("e");
        dto.setPhoneNumber("123");
        dto.setAddress("addr");

        assertEquals("e", dto.getEmail());
        assertEquals("123", dto.getPhoneNumber());
        assertEquals("addr", dto.getAddress());
    }
}
