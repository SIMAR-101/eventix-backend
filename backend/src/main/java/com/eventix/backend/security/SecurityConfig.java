package com.eventix.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Enable Global CORS configuration
            .cors(Customizer.withDefaults())
            
            // 2. Disable CSRF (We use JWTs, not browser cookies)
            .csrf(csrf -> csrf.disable())
            
            // 3. Make the API completely stateless
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 4. Configure who is allowed to go where
            .authorizeHttpRequests(auth -> auth
                // OPEN DOORS: Anyone can register an account and view the venue list
                .requestMatchers("/api/users/**", "/api/venues/**", "/api/auth/**").permitAll()
                
                // LOCKED DOORS: Everything else requires a valid JWT Token
                .anyRequest().authenticated()
            );

        return http.build();
    }

    // 5. The CORS Firewall Rules
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*")); // Allow all frontend domains
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allow all request types
        configuration.setAllowedHeaders(Arrays.asList("*")); // Allow all headers (including our JWT authorization header)
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to the entire API
        return source;
    }

    // 6. The Password Scrambler
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}