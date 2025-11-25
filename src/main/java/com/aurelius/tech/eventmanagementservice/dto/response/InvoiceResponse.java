package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private UUID id;
    private UUID paymentId;
    private String invoiceNumber;
    private BigDecimal amount;
    private String currency;
    private String filePath;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;
}



