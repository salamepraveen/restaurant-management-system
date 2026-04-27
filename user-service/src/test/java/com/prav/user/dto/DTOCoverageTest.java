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
        dto.setRole("r");
        dto.setAssignedRestaurantId(2L);
        dto.setKnownRestaurantIds(new ArrayList<>());
 
        assertEquals(1L, dto.getId());
        assertEquals("u", dto.getUsername());
        assertEquals("e", dto.getEmail());
        assertEquals("r", dto.getRole());
        assertEquals(2L, dto.getAssignedRestaurantId());
        assertNotNull(dto.getKnownRestaurantIds());
    }
}
