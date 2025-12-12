package com.aurelius.tech.eventmanagementservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventServiceItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "event_id", nullable = false)
    private UUID eventId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;
    
    @Column(name = "service_type_id", nullable = false)
    private UUID serviceTypeId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_type_id", insertable = false, updatable = false)
    private ServiceType serviceType;
    
    @Column(name = "vendor_id", nullable = false)
    private UUID vendorId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", insertable = false, updatable = false)
    private Vendor vendor;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rate; // Actual rate for this event (can differ from vendor's base rate)
    
    @Column(columnDefinition = "TEXT")
    private String notes; // Additional notes or custom requirements
    
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





