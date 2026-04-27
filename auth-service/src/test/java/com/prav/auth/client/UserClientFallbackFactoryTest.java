package com.prav.auth.client;
 
import com.prav.auth.dto.UserDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
 
class UserClientFallbackFactoryTest {
 
    @Test
    void testFallback() {
        UserClientFallbackFactory factory = new UserClientFallbackFactory();
        Throwable cause = new RuntimeException("Server down");
        UserClient fallback = factory.create(cause);
 
        UserDTO dto = new UserDTO();
        assertThrows(RuntimeException.class, () -> fallback.createUser(dto));
        assertThrows(RuntimeException.class, () -> fallback.getUserByUsername("user"));
        assertThrows(RuntimeException.class, () -> fallback.getUserByEmail("email"));
    }
}
