package com.eventix.backend.controller;

import com.eventix.backend.entity.Booking;
import com.eventix.backend.entity.User;
import com.eventix.backend.repository.BookingRepository;
import com.eventix.backend.repository.UserRepository;
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
    private JwtTokenProvider tokenProvider;

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmBooking(
            @RequestHeader("Authorization") String token, 
            @RequestBody Map<String, Object> data) {
        
        try {
            // 1. Read the Digital ID Card (Remove the "Bearer " prefix)
            String actualToken = token.substring(7);
            String email = tokenProvider.getEmailFromToken(actualToken);

            // 2. Find the user in the database
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) return ResponseEntity.badRequest().body("User not found in vault");

            // 3. Create the official booking ticket!
            Booking booking = new Booking();
            booking.setUser(userOptional.get());
            booking.setTotalAmount(Double.parseDouble(data.get("amount").toString()));
            booking.setPaymentId(data.get("paymentId").toString());

            // 4. Save to Aiven MySQL Cloud
            bookingRepository.save(booking);

            return ResponseEntity.ok("Ticket officially saved to the cloud vault!");
        } catch (Exception e) {
            System.out.println("Booking Save Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to save booking");
        }
    }
}