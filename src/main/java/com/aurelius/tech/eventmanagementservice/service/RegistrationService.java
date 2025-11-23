package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.request.CreateRegistrationRequest;
import com.aurelius.tech.eventmanagementservice.entity.Registration;
import com.aurelius.tech.eventmanagementservice.entity.Ticket;
import com.aurelius.tech.eventmanagementservice.entity.enums.RegistrationStatus;
import com.aurelius.tech.eventmanagementservice.exception.BusinessException;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.EventRepository;
import com.aurelius.tech.eventmanagementservice.repository.RegistrationRepository;
import com.aurelius.tech.eventmanagementservice.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrationService {
    
    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final QRCodeService qrCodeService;
    private final NotificationService notificationService;
    
    public RegistrationService(RegistrationRepository registrationRepository,
                              EventRepository eventRepository,
                              TicketRepository ticketRepository,
                              QRCodeService qrCodeService,
                              NotificationService notificationService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.qrCodeService = qrCodeService;
        this.notificationService = notificationService;
    }
    
    @Transactional
    public Registration createRegistration(UUID userId, CreateRegistrationRequest request) {
        if (!eventRepository.existsById(request.getEventId())) {
            throw new ResourceNotFoundException("Event", "id", request.getEventId());
        }
        
        if (registrationRepository.existsByUserIdAndEventId(userId, request.getEventId())) {
            throw new BusinessException("User is already registered for this event");
        }
        
        Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setEventId(request.getEventId());
        registration.setQuantity(request.getQuantity());
        registration.setStatus(RegistrationStatus.PENDING);
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        if (request.getTicketId() != null) {
            Ticket ticket = ticketRepository.findById(request.getTicketId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", request.getTicketId()));
            
            if (ticket.getSold() + request.getQuantity() > ticket.getQuantity()) {
                throw new BusinessException("Not enough tickets available");
            }
            
            registration.setTicketId(request.getTicketId());
            totalAmount = ticket.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
            registration.setTotalAmount(totalAmount);
        }
        
        registration = registrationRepository.save(registration);
        
        String qrCode = qrCodeService.generateQRCodeString(registration.getId());
        registration.setQrCode(qrCode);
        registration = registrationRepository.save(registration);
        
        // Send notification
        notificationService.sendRegistrationConfirmation("user@example.com", 
                java.util.Map.of("registrationId", registration.getId()));
        
        return registration;
    }
    
    public Registration getRegistrationById(UUID id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "id", id));
    }
    
    public List<Registration> getRegistrationsByUserId(UUID userId) {
        return registrationRepository.findByUserId(userId);
    }
    
    public List<Registration> getRegistrationsByEventId(UUID eventId) {
        return registrationRepository.findByEventId(eventId);
    }
    
    @Transactional
    public void cancelRegistration(UUID id) {
        Registration registration = getRegistrationById(id);
        registration.setStatus(RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
    }
    
    public Registration getRegistrationByQrCode(String qrCode) {
        return registrationRepository.findByQrCode(qrCode)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "qrCode", qrCode));
    }
}

