package com.aurelius.tech.eventmanagementservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFinancialMetricsResponse {
    private BigDecimal totalRevenue;      // Total from ticket sales
    private BigDecimal totalServiceCosts; // Total cost of all services
    private BigDecimal profit;            // Revenue - Service Costs
    private BigDecimal margin;            // (Profit / Revenue) * 100, as percentage
    private Integer totalTicketsSold;
    private Integer totalRegistrations;
}






