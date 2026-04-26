package com.prav.auth.util;

import com.prav.auth.dto.UserDTO;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    void generateAndValidateToken() {
        UserDTO user = new UserDTO();
        user.setId(123L);
        user.setUsername("testuser");
        user.setRole("ADMIN");

        String token = jwtUtil.generateToken(user);
        assertNotNull(token);

        Claims claims = jwtUtil.validate(token);
        assertEquals("123", claims.getSubject());
        assertEquals("ADMIN", claims.get("role"));
    }

    @Test
    void validate_invalidToken_throwsException() {
        assertThrows(Exception.class, () -> jwtUtil.validate("invalid.token.string"));
    }
}
