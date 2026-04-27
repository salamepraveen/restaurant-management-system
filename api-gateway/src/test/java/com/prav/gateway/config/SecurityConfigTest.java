package com.prav.gateway.config;
 
import com.prav.gateway.filter.JwtFilter2;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
class SecurityConfigTest {
 
    @Test
    void testSecurityFilterChain() {
        JwtFilter2 filter = mock(JwtFilter2.class);
        SecurityConfig config = new SecurityConfig(filter);
        ServerHttpSecurity http = ServerHttpSecurity.http();
        SecurityWebFilterChain chain = config.securityFilterChain(http);
        assertNotNull(chain);
    }
}
