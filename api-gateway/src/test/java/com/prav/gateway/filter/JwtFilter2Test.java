package com.prav.gateway.filter;
 
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
 
import javax.crypto.SecretKey;
import java.util.Date;
 
import static org.mockito.Mockito.*;
 
class JwtFilter2Test {
 
    private JwtFilter2 jwtFilter;
    private final String secret = "spidey-secret-key-stacy-gwen-spidey-secret";
 
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private WebFilterChain chain;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private HttpHeaders headers;
 
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtFilter = new JwtFilter2(secret);
        jwtFilter.init(); // Initialize the key
        when(exchange.getRequest()).thenReturn(request);
        when(request.getHeaders()).thenReturn(headers);
        when(request.getMethod()).thenReturn(org.springframework.http.HttpMethod.GET);
        when(request.getPath()).thenReturn(org.springframework.http.server.RequestPath.parse(java.net.URI.create("/test"), ""));
        when(chain.filter(any())).thenReturn(Mono.empty());
    }
 
    @Test
    void testFilter_NoAuthHeader_PassesChain() {
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        Mono<Void> result = jwtFilter.filter(exchange, chain);
        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(exchange);
    }
 
    @Test
    void testFilter_InvalidToken_ReturnsUnauthorized() {
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-token");
        org.springframework.http.server.reactive.ServerHttpResponse response = mock(org.springframework.http.server.reactive.ServerHttpResponse.class);
        org.springframework.core.io.buffer.DataBufferFactory bufferFactory = mock(org.springframework.core.io.buffer.DataBufferFactory.class);
        when(exchange.getResponse()).thenReturn(response);
        when(response.setStatusCode(any())).thenReturn(true);
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.bufferFactory()).thenReturn(bufferFactory);
        when(bufferFactory.wrap(any(byte[].class))).thenReturn(mock(org.springframework.core.io.buffer.DataBuffer.class));
        when(response.writeWith(any())).thenReturn(Mono.empty());
 
        Mono<Void> result = jwtFilter.filter(exchange, chain);
        StepVerifier.create(result).verifyComplete();
    }
 
    @Test
    void testFilter_ValidToken_Success() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        String token = Jwts.builder()
                .subject("1")
                .claim("role", "ROLE_USER")
                .issuedAt(new Date())
                .signWith(key)
                .compact();
 
        when(headers.getFirst(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
 
        Mono<Void> result = jwtFilter.filter(exchange, chain);
        StepVerifier.create(result).verifyComplete();
        verify(chain).filter(any());
    }
}
