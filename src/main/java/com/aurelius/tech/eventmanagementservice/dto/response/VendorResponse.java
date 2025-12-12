package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VendorResponse {
    private UUID id;
    private String name;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private UUID serviceTypeId;
    private String serviceTypeName;
    private BigDecimal baseRate;
    private Boolean isActive;
    private LocalDateTime createdAt;
}





