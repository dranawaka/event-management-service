package com.aurelius.tech.eventmanagementservice.repository;

import com.aurelius.tech.eventmanagementservice.entity.Event;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventStatus;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByOrganizerId(UUID organizerId);
    List<Event> findByStatus(EventStatus status);
    List<Event> findByVisibility(EventVisibility visibility);
    List<Event> findByCategoryId(UUID categoryId);
    List<Event> findByVenueId(UUID venueId);
    
    @Query("SELECT e FROM Event e WHERE e.status = :status AND e.visibility = 'PUBLIC' AND e.startDateTime >= :startDate ORDER BY e.startDateTime ASC")
    List<Event> findUpcomingPublicEvents(@Param("status") EventStatus status, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT e FROM Event e WHERE e.title LIKE %:keyword% OR e.description LIKE %:keyword%")
    List<Event> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT e FROM Event e WHERE (e.title LIKE %:keyword% OR e.description LIKE %:keyword%) AND e.visibility = 'PUBLIC'")
    List<Event> searchPublicEventsByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT e FROM Event e WHERE e.startDateTime BETWEEN :startDate AND :endDate")
    List<Event> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}









