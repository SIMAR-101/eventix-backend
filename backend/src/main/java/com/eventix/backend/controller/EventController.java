package com.eventix.backend.controller;

import com.eventix.backend.entity.Event;
import com.eventix.backend.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    // Plugs our Event "Brain" into this specific drive-thru window
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Rule: Take the event details, take the venue ID from the URL, and trigger the auto-ticket generation!
    @PostMapping("/{venueId}")
    public Event createEvent(@RequestBody Event event, @PathVariable Long venueId) {
        return eventService.createEvent(event, venueId);
    }

    // Rule: Hand out the list of all events when someone asks for it
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }
}