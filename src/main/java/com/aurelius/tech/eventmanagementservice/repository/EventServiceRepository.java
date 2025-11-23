package com.aurelius.tech.eventmanagementservice.repository;

import com.aurelius.tech.eventmanagementservice.entity.EventServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventServiceRepository extends JpaRepository<EventServiceItem, UUID> {
    List<EventServiceItem> findByEventId(UUID eventId);
}

