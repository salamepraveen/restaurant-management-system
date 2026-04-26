package com.prav.auth.client;

import com.prav.auth.dto.UserDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientCoverageTest {

    @Test
    void testFallbackFactory() {
        UserClientFallbackFactory factory = new UserClientFallbackFactory();
        UserClient fallback = factory.create(new RuntimeException("Service Down"));
        assertNotNull(fallback);
        
        assertThrows(RuntimeException.class, () -> fallback.getUserByUsername("test"));
        assertThrows(RuntimeException.class, () -> fallback.getUserByEmail("test@test.com"));
        assertThrows(RuntimeException.class, () -> fallback.createUser(new UserDTO()));
    }

    @Test
    void testFallbackDirect() {
        UserClientFallback fallback = new UserClientFallback();
        assertThrows(RuntimeException.class, () -> fallback.getUserByUsername("test"));
        assertThrows(RuntimeException.class, () -> fallback.getUserByEmail("test@test.com"));
        assertThrows(RuntimeException.class, () -> fallback.createUser(new UserDTO()));
    }
}
