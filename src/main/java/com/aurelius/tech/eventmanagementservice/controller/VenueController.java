package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.entity.Venue;
import com.aurelius.tech.eventmanagementservice.service.VenueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/venues")
public class VenueController {
    
    private final VenueService venueService;
    
    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }
    
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable UUID id) {
        return ResponseEntity.ok(venueService.getVenueById(id));
    }
    
    @PostMapping
    public ResponseEntity<Venue> createVenue(@Valid @RequestBody Venue venue) {
        Venue createdVenue = venueService.createVenue(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable UUID id, @Valid @RequestBody Venue venue) {
        return ResponseEntity.ok(venueService.updateVenue(id, venue));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable UUID id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}








