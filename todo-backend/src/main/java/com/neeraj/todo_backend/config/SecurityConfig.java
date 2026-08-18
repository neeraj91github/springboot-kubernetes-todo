package com.neeraj.todo_backend.config;

import com.neeraj.todo_backend.security.JWTAuthFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // disable CSRF because we’re a stateless REST API
                .csrf(AbstractHttpConfigurer::disable)
                // configure URL authorization
                .authorizeHttpRequests(auth -> auth
                        // allow test endpoint without any auth
                        .requestMatchers("/api/auth/test").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        // everything else requires authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
