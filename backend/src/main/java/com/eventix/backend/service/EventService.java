package com.eventix.backend.service;

import com.eventix.backend.entity.Event;
import com.eventix.backend.entity.Ticket;
import com.eventix.backend.entity.Venue;
import com.eventix.backend.repository.EventRepository;
import com.eventix.backend.repository.TicketRepository;
import com.eventix.backend.repository.VenueRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final VenueRepository venueRepository;

    public EventService(EventRepository eventRepository, TicketRepository ticketRepository, VenueRepository venueRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.venueRepository = venueRepository;
    }

    // Rule: Create Event AND auto-generate tickets based on venue capacity
    public Event createEvent(Event event, Long venueId) {
        // 1. Find the venue to get its capacity
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("Venue not found"));

        // 2. Attach the venue to the event and save the event
        event.setVenue(venue);
        Event savedEvent = eventRepository.save(event);

        // 3. Generate tickets automatically!
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= venue.getTotalCapacity(); i++) {
            Ticket ticket = new Ticket();
            ticket.setEvent(savedEvent);
            ticket.setSeatNumber("Seat-" + i);
            ticket.setPrice(50.0); // Setting a default price for the seats
            tickets.add(ticket);
        }
        
        // Save all those generated tickets to the database at once
        ticketRepository.saveAll(tickets);

        return savedEvent;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
}