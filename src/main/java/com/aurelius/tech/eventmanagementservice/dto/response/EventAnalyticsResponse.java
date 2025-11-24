package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAnalyticsResponse {
    private UUID eventId;
    private String eventTitle;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    
    // Registration metrics
    private Integer totalRegistrations;
    private Integer confirmedRegistrations;
    private Integer cancelledRegistrations;
    private Integer pendingRegistrations;
    
    // Ticket sales metrics
    private Integer totalTicketsSold;
    private Integer totalTicketsAvailable;
    private BigDecimal ticketSalesRevenue;
    
    // Financial metrics
    private BigDecimal totalRevenue;
    private BigDecimal totalServiceCosts;
    private BigDecimal profit;
    private BigDecimal margin;
    
    // Attendance metrics
    private Integer checkedInCount;
    private Integer noShowCount;
    private Double attendanceRate;
    
    // Time series data (daily registrations)
    private List<Map<String, Object>> registrationTrends;
}


