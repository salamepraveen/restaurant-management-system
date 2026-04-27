package com.prav.gateway.config;

import com.prav.gateway.filter.JwtFilter2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtFilter2 jwtFilter;

    public SecurityConfig(JwtFilter2 jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .exceptionHandling(exceptionHandling -> 
                    exceptionHandling.authenticationEntryPoint((exchange, e) -> {
                        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                        return reactor.core.publisher.Mono.empty();
                    })
                )
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/pizzas/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/pizzas/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/pizzas/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/pizzas/**").hasRole("ADMIN")
                        .pathMatchers("/pizzas/toppings").hasRole("ADMIN")
                        .pathMatchers("/orders/reports/**").hasAnyRole("ADMIN", "STAFF")
                        .pathMatchers("/orders/**").authenticated()
                        .pathMatchers("/users/**").authenticated()
                        .anyExchange().permitAll()
                )
                .build();
    }
}