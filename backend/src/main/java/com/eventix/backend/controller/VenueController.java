package com.eventix.backend.controller;

import com.eventix.backend.entity.Venue;
import com.eventix.backend.service.VenueService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    // Plugs our Venue "Brain" (Service) into the drive-thru window
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    // Rule: When a website sends a POST request here, create a new venue
    @PostMapping
    public Venue createVenue(@RequestBody Venue venue) {
        return venueService.createVenue(venue);
    }

    // Rule: When a website sends a GET request here, hand them the list of all venues
    @GetMapping
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }
}