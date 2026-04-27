package com.prav.gateway.filter;
 
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
 
@Component
public class JwtFilter2 implements WebFilter {
 
    private static final Logger log = LoggerFactory.getLogger(JwtFilter2.class);
    private final String secret;
    private SecretKey key;
 
    public JwtFilter2(@org.springframework.beans.factory.annotation.Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }
 
    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("======= JwtFilter INITIALIZED =======");
    }
 
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();
 
        log.info("JwtFilter: {} {}", method, path);
 
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }
 
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }
 
        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
 
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            
            if (role != null && !role.startsWith("ROLE_")) {
                role = "ROLE_" + role.toUpperCase();
            }

            log.info("======= JWT OK: userId={} role={} =======", userId, role);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority(role))
            );
 
            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
 
        } catch (Exception e) {
            log.error(" JWT ERROR: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = "{\"error\": \"Unauthorized\", \"message\": \"" + e.getMessage() + "\"}";
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(body.getBytes())));
        }
    }
}
