package com.aurelius.tech.eventmanagementservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateRegistrationRequest {
    
    @NotNull(message = "Event ID is required")
    private UUID eventId;
    
    private UUID ticketId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String promoCode;
}






