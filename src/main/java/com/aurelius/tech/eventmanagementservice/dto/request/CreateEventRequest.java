package com.aurelius.tech.eventmanagementservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CreateEventRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Organizer ID is required")
    private UUID organizerId;
    
    private UUID venueId;
    
    private UUID categoryId;
    
    @NotNull(message = "Start date/time is required")
    private LocalDateTime startDateTime;
    
    @NotNull(message = "End date/time is required")
    private LocalDateTime endDateTime;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    private String visibility = "PUBLIC";
    
    private String imageUrl;
    
    @Valid
    private List<EventServiceRequest> services = new ArrayList<>();
}




