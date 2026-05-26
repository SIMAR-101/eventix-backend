package com.eventix.backend.controller;

import com.eventix.backend.dto.LoginRequest;
import com.eventix.backend.entity.User;
import com.eventix.backend.repository.UserRepository;
import com.eventix.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        
        // 1. Find the user by their email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 2. Check if the password they typed matches the scrambled hash in the database
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                
                // 3. Password is correct! Generate the ID card (Token)
                String token = tokenProvider.generateToken(user.getEmail());
                
                // Send the token back to React!
                return ResponseEntity.ok("{\"token\": \"" + token + "\"}");
            }
        }

        // 4. Wrong email or password
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }
}