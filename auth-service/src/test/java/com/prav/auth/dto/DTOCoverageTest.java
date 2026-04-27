package com.prav.auth.dto;
 
import org.junit.jupiter.api.Test;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
 
class DTOCoverageTest {
 
    @Test
    void testDTOs() {
        AuthRequest req = new AuthRequest();
        req.setUsername("u");
        req.setPassword("p");
        req.setEmail("e");
        assertEquals("u", req.getUsername());
        assertEquals("p", req.getPassword());
        assertEquals("e", req.getEmail());
 
        AuthResponse res = new AuthResponse();
        res.setToken("t");
        res.setUsername("u");
        res.setEmail("e");
        res.setUserId(1L);
        res.setRole("r");
        res.setAssignedRestaurantId(2L);
        res.setKnownRestaurantIds(Collections.singletonList(3L));
        assertEquals("t", res.getToken());
        assertEquals("u", res.getUsername());
        assertEquals("e", res.getEmail());
        assertEquals(1L, res.getUserId());
        assertEquals("r", res.getRole());
        assertEquals(2L, res.getAssignedRestaurantId());
        assertEquals(3L, res.getKnownRestaurantIds().get(0));
 
        OtpRequestDTO oreq = new OtpRequestDTO();
        oreq.setUsername("u");
        oreq.setEmail("e");
        oreq.setPassword("p");
        assertEquals("u", oreq.getUsername());
        assertEquals("e", oreq.getEmail());
        assertEquals("p", oreq.getPassword());
 
        OtpVerifyDTO vreq = new OtpVerifyDTO();
        vreq.setEmail("e");
        vreq.setOtp("1");
        assertEquals("e", vreq.getEmail());
        assertEquals("1", vreq.getOtp());
 
        UserDTO u = new UserDTO();
        u.setId(1L);
        u.setUsername("u");
        u.setEmail("e");
        u.setPassword("p");
        u.setRole("r");
        u.setAssignedRestaurantId(2L);
        u.setKnownRestaurantIds(Collections.singletonList(3L));
        assertEquals(1L, u.getId());
        assertEquals("u", u.getUsername());
        assertEquals("e", u.getEmail());
        assertEquals("p", u.getPassword());
        assertEquals("r", u.getRole());
        assertEquals(2L, u.getAssignedRestaurantId());
        assertEquals(3L, u.getKnownRestaurantIds().get(0));
    }
}
