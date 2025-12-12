package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.dto.request.CreateTicketRequest;
import com.aurelius.tech.eventmanagementservice.entity.Ticket;
import com.aurelius.tech.eventmanagementservice.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TicketController {
    
    private final TicketService ticketService;
    
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
    
    @GetMapping("/events/{eventId}/tickets")
    public ResponseEntity<List<Ticket>> getTicketsByEvent(@PathVariable UUID eventId) {
        return ResponseEntity.ok(ticketService.getTicketsByEventId(eventId));
    }
    
    @PostMapping("/events/{eventId}/tickets")
    public ResponseEntity<Ticket> createTicket(@PathVariable UUID eventId, @Valid @RequestBody CreateTicketRequest request) {
        request.setEventId(eventId);
        Ticket ticket = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ticket);
    }
    
    @GetMapping("/tickets/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }
    
    @PutMapping("/tickets/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable UUID id, @Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.ok(ticketService.updateTicket(id, request));
    }
    
    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}









