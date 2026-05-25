package com.eventix.backend.service;

import com.eventix.backend.entity.Venue;
import com.eventix.backend.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VenueService {

    private final VenueRepository venueRepository;

    // This wires our filing cabinet into the service!
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    // Rule: Save a new venue to the database
    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    // Rule: Fetch a list of all venues
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
}