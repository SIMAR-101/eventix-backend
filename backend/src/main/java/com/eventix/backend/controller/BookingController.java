package com.eventix.backend.controller;

import com.eventix.backend.entity.Booking;
import com.eventix.backend.entity.User;
import com.eventix.backend.entity.Venue;
import com.eventix.backend.repository.BookingRepository;
import com.eventix.backend.repository.UserRepository;
import com.eventix.backend.repository.VenueRepository;
import com.eventix.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VenueRepository venueRepository; // NEW: We need to talk to the venues table

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(
            @RequestHeader("Authorization") String token, 
            @RequestBody Map<String, Object> data) {
        
        try {
            // 1. Authenticate the User
            String actualToken = token.substring(7);
            String email = tokenProvider.getEmailFromToken(actualToken);

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) return ResponseEntity.badRequest().body("User not found in vault");

            // 2. Find the specific Venue being booked
            Long venueId = Long.parseLong(data.get("venueId").toString());
            Optional<Venue> venueOptional = venueRepository.findById(venueId);
            
            if (!venueOptional.isPresent()) {
                return ResponseEntity.badRequest().body("Venue not found");
            }

            Venue venue = venueOptional.get();

            // 3. INVENTORY CHECK: Are there tickets left?
            if (venue.getTotalCapacity() <= 0) {
                return ResponseEntity.badRequest().body("Venue is sold out!");
            }

            // 4. Create the Booking Record
            Booking booking = new Booking();
            booking.setUser(userOptional.get());
            booking.setVenue(venue); // Link the venue!
            booking.setTotalAmount(Double.parseDouble(data.get("amount").toString()));
            booking.setPaymentId(data.get("paymentId").toString());

            bookingRepository.save(booking);

            // 5. INVENTORY REDUCTION: Subtract 1 ticket and save the updated venue
            venue.setTotalCapacity(venue.getTotalCapacity() - 1);
            venueRepository.save(venue);

            return ResponseEntity.ok("Ticket saved and inventory updated!");
        } catch (Exception e) {
            System.out.println("Booking Save Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to save booking");
        }
    }
}