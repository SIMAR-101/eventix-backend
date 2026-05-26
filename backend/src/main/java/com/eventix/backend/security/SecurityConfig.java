package com.eventix.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (We use JWTs, not browser cookies!)
            .csrf(csrf -> csrf.disable())
            
            // 2. Make the API completely stateless (No memory between requests)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. Configure who is allowed to go where
            .authorizeHttpRequests(auth -> auth
                // OPEN DOORS: Anyone can register an account and view the venue list
                .requestMatchers("/api/users/**", "/api/venues/**").permitAll()
                
                // LOCKED DOORS: Everything else requires a valid JWT Token
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // 4. The Password Scrambler
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is the industry standard hashing algorithm
        return new BCryptPasswordEncoder();
    }
}