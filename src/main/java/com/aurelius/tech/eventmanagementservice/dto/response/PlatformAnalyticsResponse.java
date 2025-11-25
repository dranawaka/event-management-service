package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformAnalyticsResponse {
    // User statistics
    private Integer totalUsers;
    private Integer totalOrganizers;
    private Integer totalAttendees;
    private Integer activeUsers;
    
    // Event statistics
    private Integer totalEvents;
    private Integer publishedEvents;
    private Integer completedEvents;
    private Integer upcomingEvents;
    
    // Financial statistics
    private BigDecimal totalPlatformRevenue;
    private BigDecimal totalPaymentsProcessed;
    private BigDecimal totalRefunds;
    private BigDecimal platformCommission;
    
    // Registration statistics
    private Integer totalRegistrations;
    private Integer totalTicketsSold;
    private Double averageEventAttendance;
    
    // Category distribution
    private List<Map<String, Object>> eventsByCategory;
    
    // Revenue trends (monthly)
    private List<Map<String, Object>> platformRevenueTrends;
}



