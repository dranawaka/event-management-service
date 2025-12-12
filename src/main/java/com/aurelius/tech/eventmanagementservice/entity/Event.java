package com.aurelius.tech.eventmanagementservice.entity;

import com.aurelius.tech.eventmanagementservice.entity.enums.EventStatus;
import com.aurelius.tech.eventmanagementservice.entity.enums.EventVisibility;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", insertable = false, updatable = false)
    private User organizer;
    
    @Column(name = "venue_id")
    private UUID venueId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", insertable = false, updatable = false)
    private Venue venue;
    
    @Column(name = "category_id")
    private UUID categoryId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
    
    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;
    
    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status = EventStatus.DRAFT;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventVisibility visibility = EventVisibility.PUBLIC;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}








