package com.eventix.backend.controller;

import com.eventix.backend.entity.User;
import com.eventix.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Rule: Take the user details from the request and save them to the database
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }
}