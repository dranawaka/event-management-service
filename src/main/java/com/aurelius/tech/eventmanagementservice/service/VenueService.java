package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.entity.Venue;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.VenueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VenueService {
    
    private final VenueRepository venueRepository;
    
    public VenueService(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }
    
    @Transactional
    public Venue createVenue(Venue venue) {
        return venueRepository.save(venue);
    }
    
    public Venue getVenueById(UUID id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue", "id", id));
    }
    
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }
    
    @Transactional
    public Venue updateVenue(UUID id, Venue venueDetails) {
        Venue venue = getVenueById(id);
        venue.setName(venueDetails.getName());
        venue.setAddress(venueDetails.getAddress());
        venue.setCity(venueDetails.getCity());
        venue.setState(venueDetails.getState());
        venue.setCountry(venueDetails.getCountry());
        venue.setZipCode(venueDetails.getZipCode());
        venue.setCapacity(venueDetails.getCapacity());
        venue.setLatitude(venueDetails.getLatitude());
        venue.setLongitude(venueDetails.getLongitude());
        
        return venueRepository.save(venue);
    }
    
    @Transactional
    public void deleteVenue(UUID id) {
        Venue venue = getVenueById(id);
        venueRepository.delete(venue);
    }
}








