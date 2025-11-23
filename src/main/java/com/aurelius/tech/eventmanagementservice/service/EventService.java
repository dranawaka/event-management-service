package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.request.CreateEventRequest;
import com.aurelius.tech.eventmanagementservice.entity.Event;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventStatus;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventVisibility;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    
    private final EventRepository eventRepository;
    
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    @Transactional
    public Event createEvent(CreateEventRequest request) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setOrganizerId(request.getOrganizerId());
        event.setVenueId(request.getVenueId());
        event.setCategoryId(request.getCategoryId());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setCapacity(request.getCapacity());
        event.setStatus(EventStatus.DRAFT);
        event.setVisibility(EventVisibility.valueOf(request.getVisibility().toUpperCase()));
        event.setImageUrl(request.getImageUrl());
        
        return eventRepository.save(event);
    }
    
    public Event getEventById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", id));
    }
    
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    public List<Event> getEventsByOrganizer(UUID organizerId) {
        return eventRepository.findByOrganizerId(organizerId);
    }
    
    public List<Event> searchEvents(String keyword) {
        return eventRepository.searchByKeyword(keyword);
    }
    
    public List<Event> getUpcomingPublicEvents() {
        return eventRepository.findUpcomingPublicEvents(EventStatus.PUBLISHED, LocalDateTime.now());
    }
    
    @Transactional
    public Event updateEvent(UUID id, CreateEventRequest request) {
        Event event = getEventById(id);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setVenueId(request.getVenueId());
        event.setCategoryId(request.getCategoryId());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setCapacity(request.getCapacity());
        event.setVisibility(EventVisibility.valueOf(request.getVisibility().toUpperCase()));
        event.setImageUrl(request.getImageUrl());
        
        return eventRepository.save(event);
    }
    
    @Transactional
    public void deleteEvent(UUID id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }
    
    @Transactional
    public Event publishEvent(UUID id) {
        Event event = getEventById(id);
        event.setStatus(EventStatus.PUBLISHED);
        return eventRepository.save(event);
    }
    
    @Transactional
    public Event cancelEvent(UUID id) {
        Event event = getEventById(id);
        event.setStatus(EventStatus.CANCELLED);
        return eventRepository.save(event);
    }
}

