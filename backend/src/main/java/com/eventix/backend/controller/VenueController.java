package com.eventix.backend.controller;

import com.eventix.backend.entity.User;
import com.eventix.backend.entity.Venue;
import com.eventix.backend.repository.UserRepository;
import com.eventix.backend.repository.VenueRepository;
import com.eventix.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/venues")
@CrossOrigin(origins = "*")
public class VenueController {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // 1. PUBLIC ROUTE: Consumers still need to see all venues on the main site
    @GetMapping
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    // 2. SECURE B2B ROUTE: ONLY verified Organizers can create a venue
    @PostMapping
    public ResponseEntity<?> createVenue(
            @RequestHeader("Authorization") String token, 
            @RequestBody Venue venue) {
        try {
            // Read the digital ID card
            String actualToken = token.substring(7);
            String email = tokenProvider.getEmailFromToken(actualToken);

            Optional<User> userOpt = userRepository.findByEmail(email);
            if (!userOpt.isPresent()) return ResponseEntity.badRequest().body("User not found");

            User user = userOpt.get();

            // THE SAAS GUARDRAIL: Reject anyone who isn't an Organizer
            if (!"ORGANIZER".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                     .body("Error: Only verified organizers can create venues.");
            }

            // Cryptographically link the new venue to the business owner
            venue.setOrganizer(user);
            Venue savedVenue = venueRepository.save(venue);

            return ResponseEntity.ok(savedVenue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to verify organizer identity.");
        }
    }
}