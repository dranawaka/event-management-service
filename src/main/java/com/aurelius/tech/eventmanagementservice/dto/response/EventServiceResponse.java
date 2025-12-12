package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class EventServiceResponse {
    private UUID id;
    private UUID eventId;
    private UUID serviceTypeId;
    private String serviceTypeName;
    private UUID vendorId;
    private String vendorName;
    private BigDecimal rate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}






