package com.prav.auth.dto;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class DTOCoverageTest {

    @Test
    void testAuthRequest() {
        AuthRequest dto = new AuthRequest();
        dto.setUsername("user");
        dto.setPassword("pass");
        dto.setEmail("test@test.com");
        assertEquals("user", dto.getUsername());
        assertEquals("pass", dto.getPassword());
        assertEquals("test@test.com", dto.getEmail());
    }

    @Test
    void testAuthResponse() {
        AuthResponse dto = new AuthResponse();
        dto.setUserId(1L);
        dto.setUsername("user");
        dto.setEmail("test@test.com");
        dto.setToken("tk");
        dto.setRole("ADMIN");
        dto.setAssignedRestaurantId(10L);
        dto.setKnownRestaurantIds(Collections.singletonList(10L));

        assertEquals(1L, dto.getUserId());
        assertEquals("user", dto.getUsername());
        assertEquals("test@test.com", dto.getEmail());
        assertEquals("tk", dto.getToken());
        assertEquals("ADMIN", dto.getRole());
        assertEquals(10L, dto.getAssignedRestaurantId());
        assertEquals(1, dto.getKnownRestaurantIds().size());
    }

    @Test
    void testOtpRequestDTO() {
        OtpRequestDTO dto = new OtpRequestDTO();
        dto.setUsername("user");
        dto.setEmail("email");
        dto.setPassword("pass");
        assertEquals("user", dto.getUsername());
        assertEquals("email", dto.getEmail());
        assertEquals("pass", dto.getPassword());
    }

    @Test
    void testOtpVerifyDTO() {
        OtpVerifyDTO dto = new OtpVerifyDTO();
        dto.setEmail("email");
        dto.setOtp("1234");
        assertEquals("email", dto.getEmail());
        assertEquals("1234", dto.getOtp());
    }

    @Test
    void testUserDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setUsername("u");
        dto.setPassword("p");
        dto.setEmail("e");
        dto.setRole("r");
        dto.setAssignedRestaurantId(2L);
        dto.setKnownRestaurantIds(Collections.emptyList());

        assertEquals(1L, dto.getId());
        assertEquals("u", dto.getUsername());
        assertEquals("p", dto.getPassword());
        assertEquals("e", dto.getEmail());
        assertEquals("r", dto.getRole());
        assertEquals(2L, dto.getAssignedRestaurantId());
        assertNotNull(dto.getKnownRestaurantIds());
    }
}
