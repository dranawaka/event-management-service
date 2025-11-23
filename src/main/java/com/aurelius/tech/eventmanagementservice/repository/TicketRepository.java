package com.aurelius.tech.eventmanagementservice.repository;

import com.aurelius.tech.eventmanagementservice.entity.Ticket;
import com.aurelius.tech.eventmanagementservice.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByEventId(UUID eventId);
    List<Ticket> findByEventIdAndStatus(UUID eventId, TicketStatus status);
}


