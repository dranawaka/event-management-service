package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.dto.request.CreateEventRequest;
import com.aurelius.tech.eventmanagementservice.dto.response.EventFinancialMetricsResponse;
import com.aurelius.tech.eventmanagementservice.dto.response.EventServiceResponse;
import com.aurelius.tech.eventmanagementservice.entity.Event;
import com.aurelius.tech.eventmanagementservice.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    
    private final EventService eventService;
    
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(@RequestParam(required = false) String visibility) {
        if (visibility != null && !visibility.isEmpty()) {
            try {
                com.aurelius.tech.eventmanagementservice.entity.enums.EventVisibility visibilityEnum = 
                    com.aurelius.tech.eventmanagementservice.entity.enums.EventVisibility.valueOf(visibility.toUpperCase());
                return ResponseEntity.ok(eventService.getAllEvents(visibilityEnum));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.ok(eventService.getAllEvents());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }
    
    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody CreateEventRequest request) {
        Event event = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable UUID id, @Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, request));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/publish")
    public ResponseEntity<Event> publishEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.publishEvent(id));
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Event> cancelEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.cancelEvent(id));
    }
    
    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Event>> getEventsByOrganizer(@PathVariable UUID organizerId) {
        return ResponseEntity.ok(eventService.getEventsByOrganizer(organizerId));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Event>> searchEvents(@RequestParam String keyword) {
        return ResponseEntity.ok(eventService.searchEvents(keyword));
    }
    
    @GetMapping("/{id}/services")
    public ResponseEntity<List<EventServiceResponse>> getEventServices(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventServices(id));
    }
    
    @GetMapping("/{id}/financial-metrics")
    public ResponseEntity<EventFinancialMetricsResponse> getEventFinancialMetrics(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEventFinancialMetrics(id));
    }
}




