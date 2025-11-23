package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.request.CreateEventRequest;
import com.aurelius.tech.eventmanagementservice.dto.request.EventServiceRequest;
import com.aurelius.tech.eventmanagementservice.dto.response.EventFinancialMetricsResponse;
import com.aurelius.tech.eventmanagementservice.dto.response.EventServiceResponse;
import com.aurelius.tech.eventmanagementservice.entity.Event;
import com.aurelius.tech.eventmanagementservice.entity.EventServiceItem;
import com.aurelius.tech.eventmanagementservice.entity.Payment;
import com.aurelius.tech.eventmanagementservice.entity.Registration;
import com.aurelius.tech.eventmanagementservice.entity.Vendor;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventStatus;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventVisibility;
import com.aurelius.tech.eventmanagementservice.entity.enums.PaymentStatus;
import com.aurelius.tech.eventmanagementservice.exception.BusinessException;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.EventRepository;
import com.aurelius.tech.eventmanagementservice.repository.EventServiceRepository;
import com.aurelius.tech.eventmanagementservice.repository.PaymentRepository;
import com.aurelius.tech.eventmanagementservice.repository.RegistrationRepository;
import com.aurelius.tech.eventmanagementservice.repository.ServiceTypeRepository;
import com.aurelius.tech.eventmanagementservice.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {
    
    private final EventRepository eventRepository;
    private final EventServiceRepository eventServiceRepository;
    private final PaymentRepository paymentRepository;
    private final RegistrationRepository registrationRepository;
    private final VendorRepository vendorRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    
    public EventService(EventRepository eventRepository, 
                        EventServiceRepository eventServiceRepository,
                        PaymentRepository paymentRepository,
                        RegistrationRepository registrationRepository,
                        VendorRepository vendorRepository,
                        ServiceTypeRepository serviceTypeRepository) {
        this.eventRepository = eventRepository;
        this.eventServiceRepository = eventServiceRepository;
        this.paymentRepository = paymentRepository;
        this.registrationRepository = registrationRepository;
        this.vendorRepository = vendorRepository;
        this.serviceTypeRepository = serviceTypeRepository;
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
        
        event = eventRepository.save(event);
        
        // Save event services with vendor validation
        if (request.getServices() != null && !request.getServices().isEmpty()) {
            for (EventServiceRequest serviceRequest : request.getServices()) {
                // Validate service type exists
                serviceTypeRepository.findById(serviceRequest.getServiceTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("ServiceType", "id", serviceRequest.getServiceTypeId()));
                
                // Validate vendor exists and belongs to the service type
                Vendor vendor = vendorRepository.findById(serviceRequest.getVendorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", serviceRequest.getVendorId()));
                
                if (!vendor.getServiceTypeId().equals(serviceRequest.getServiceTypeId())) {
                    throw new BusinessException("Vendor does not provide the specified service type");
                }
                
                if (!vendor.getIsActive()) {
                    throw new BusinessException("Vendor is not active");
                }
                
                EventServiceItem eventServiceItem = new EventServiceItem();
                eventServiceItem.setEventId(event.getId());
                eventServiceItem.setServiceTypeId(serviceRequest.getServiceTypeId());
                eventServiceItem.setVendorId(serviceRequest.getVendorId());
                eventServiceItem.setRate(serviceRequest.getRate());
                eventServiceItem.setNotes(serviceRequest.getNotes());
                eventServiceRepository.save(eventServiceItem);
            }
        }
        
        return event;
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
    
    public List<EventServiceResponse> getEventServices(UUID eventId) {
        List<EventServiceItem> services = eventServiceRepository.findByEventId(eventId);
        return services.stream()
                .map(this::mapToEventServiceResponse)
                .collect(Collectors.toList());
    }
    
    public EventFinancialMetricsResponse getEventFinancialMetrics(UUID eventId) {
        // Validate event exists
        getEventById(eventId);
        
        // Calculate total revenue from successful payments
        List<Payment> successfulPayments = paymentRepository.findAllByRegistration_EventIdAndStatus(
            eventId, PaymentStatus.SUCCESS);
        BigDecimal totalRevenue = successfulPayments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate total service costs
        List<EventServiceItem> services = eventServiceRepository.findByEventId(eventId);
        BigDecimal totalServiceCosts = services.stream()
            .map(EventServiceItem::getRate)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate profit
        BigDecimal profit = totalRevenue.subtract(totalServiceCosts);
        
        // Calculate margin percentage
        BigDecimal margin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            margin = profit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Get total registrations and tickets sold
        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        int totalTicketsSold = registrations.stream()
            .mapToInt(Registration::getQuantity)
            .sum();
        
        return new EventFinancialMetricsResponse(
            totalRevenue,
            totalServiceCosts,
            profit,
            margin,
            totalTicketsSold,
            registrations.size()
        );
    }
    
    private EventServiceResponse mapToEventServiceResponse(EventServiceItem eventServiceItem) {
        EventServiceResponse response = new EventServiceResponse();
        response.setId(eventServiceItem.getId());
        response.setEventId(eventServiceItem.getEventId());
        response.setServiceTypeId(eventServiceItem.getServiceTypeId());
        if (eventServiceItem.getServiceType() != null) {
            response.setServiceTypeName(eventServiceItem.getServiceType().getName());
        }
        response.setVendorId(eventServiceItem.getVendorId());
        if (eventServiceItem.getVendor() != null) {
            response.setVendorName(eventServiceItem.getVendor().getName());
        }
        response.setRate(eventServiceItem.getRate());
        response.setNotes(eventServiceItem.getNotes());
        response.setCreatedAt(eventServiceItem.getCreatedAt());
        response.setUpdatedAt(eventServiceItem.getUpdatedAt());
        return response;
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
        
        // Update services - delete existing and create new ones
        if (request.getServices() != null) {
            List<EventServiceItem> existingServices = eventServiceRepository.findByEventId(id);
            eventServiceRepository.deleteAll(existingServices);
            
            for (EventServiceRequest serviceRequest : request.getServices()) {
                // Validate service type exists
                serviceTypeRepository.findById(serviceRequest.getServiceTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("ServiceType", "id", serviceRequest.getServiceTypeId()));
                
                // Validate vendor exists and belongs to the service type
                Vendor vendor = vendorRepository.findById(serviceRequest.getVendorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", serviceRequest.getVendorId()));
                
                if (!vendor.getServiceTypeId().equals(serviceRequest.getServiceTypeId())) {
                    throw new BusinessException("Vendor does not provide the specified service type");
                }
                
                if (!vendor.getIsActive()) {
                    throw new BusinessException("Vendor is not active");
                }
                
                EventServiceItem eventServiceItem = new EventServiceItem();
                eventServiceItem.setEventId(id);
                eventServiceItem.setServiceTypeId(serviceRequest.getServiceTypeId());
                eventServiceItem.setVendorId(serviceRequest.getVendorId());
                eventServiceItem.setRate(serviceRequest.getRate());
                eventServiceItem.setNotes(serviceRequest.getNotes());
                eventServiceRepository.save(eventServiceItem);
            }
        }
        
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

