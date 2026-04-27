package com.prav.auth.util;
 
import com.prav.auth.dto.UserDTO;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
class JwtUtilTest {
 
    private final JwtUtil jwtUtil = new JwtUtil("spidey-secret-key-stacy-gwen-spidey-secret");
 
    @Test
    void testTokenGenerationAndValidation() {
        UserDTO user = new UserDTO();
        user.setId(1L);
        user.setRole("USER");
        user.setAssignedRestaurantId(2L);
 
        String token = jwtUtil.generateToken(user);
        assertNotNull(token);
 
        Claims claims = jwtUtil.validate(token);
        assertEquals("1", claims.getSubject());
        assertEquals("USER", claims.get("role"));
        assertEquals(2, ((Number)claims.get("assignedRestaurantId")).intValue());
    }
}
