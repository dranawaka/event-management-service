package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.request.CreateTicketRequest;
import com.aurelius.tech.eventmanagementservice.entity.Ticket;
import com.aurelius.tech.eventmanagementservice.entity.enums.TicketStatus;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TicketService {
    
    private final TicketRepository ticketRepository;
    
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
    
    @Transactional
    public Ticket createTicket(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setEventId(request.getEventId());
        ticket.setName(request.getName());
        ticket.setDescription(request.getDescription());
        ticket.setPrice(request.getPrice());
        ticket.setQuantity(request.getQuantity());
        ticket.setSold(0);
        ticket.setSaleStartDate(request.getSaleStartDate());
        ticket.setSaleEndDate(request.getSaleEndDate());
        ticket.setStatus(TicketStatus.AVAILABLE);
        
        return ticketRepository.save(ticket);
    }
    
    public Ticket getTicketById(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
    }
    
    public List<Ticket> getTicketsByEventId(UUID eventId) {
        return ticketRepository.findByEventId(eventId);
    }
    
    @Transactional
    public Ticket updateTicket(UUID id, CreateTicketRequest request) {
        Ticket ticket = getTicketById(id);
        ticket.setName(request.getName());
        ticket.setDescription(request.getDescription());
        ticket.setPrice(request.getPrice());
        ticket.setQuantity(request.getQuantity());
        ticket.setSaleStartDate(request.getSaleStartDate());
        ticket.setSaleEndDate(request.getSaleEndDate());
        
        return ticketRepository.save(ticket);
    }
    
    @Transactional
    public void deleteTicket(UUID id) {
        Ticket ticket = getTicketById(id);
        ticketRepository.delete(ticket);
    }
}


