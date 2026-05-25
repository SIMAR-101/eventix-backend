package com.eventix.backend.controller;

import com.eventix.backend.entity.Booking;
import com.eventix.backend.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    // Plugs our secure transaction "Brain" into the checkout window
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Rule: Take the User ID from the URL, take the list of Ticket IDs from the body, and process the sale!
    @PostMapping("/user/{userId}")
    public Booking createBooking(@PathVariable Long userId, @RequestBody List<Long> ticketIds) {
        return bookingService.createBooking(userId, ticketIds);
    }
}