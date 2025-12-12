package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerDashboardResponse {
    private UUID organizerId;
    private String organizerName;
    
    // Overall statistics
    private Integer totalEvents;
    private Integer activeEvents;
    private Integer completedEvents;
    private Integer cancelledEvents;
    
    // Financial summary
    private BigDecimal totalRevenue;
    private BigDecimal totalServiceCosts;
    private BigDecimal totalProfit;
    private BigDecimal averageMargin;
    
    // Registration summary
    private Integer totalRegistrations;
    private Integer totalTicketsSold;
    
    // Recent events performance
    private List<Map<String, Object>> recentEventsPerformance;
    
    // Revenue trends (monthly)
    private List<Map<String, Object>> revenueTrends;
}





