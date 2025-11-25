package com.aurelius.tech.eventmanagementservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePayoutRequest {
    @NotNull(message = "Organizer ID is required")
    private UUID organizerId;
    
    private UUID eventId; // Optional: for event-specific payout
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private String currency = "USD";
    
    private String paymentMethod;
    
    private String notes;
}



