package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.response.EventAnalyticsResponse;
import com.aurelius.tech.eventmanagementservice.dto.response.OrganizerDashboardResponse;
import com.aurelius.tech.eventmanagementservice.dto.response.PlatformAnalyticsResponse;
import com.aurelius.tech.eventmanagementservice.entity.*;
import com.aurelius.tech.eventmanagementservice.entity.enums.*;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CategoryRepository categoryRepository;
    private final EventServiceRepository eventServiceRepository;
    
    public AnalyticsService(EventRepository eventRepository,
                           RegistrationRepository registrationRepository,
                           PaymentRepository paymentRepository,
                           UserRepository userRepository,
                           TicketRepository ticketRepository,
                           CategoryRepository categoryRepository,
                           EventServiceRepository eventServiceRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.categoryRepository = categoryRepository;
        this.eventServiceRepository = eventServiceRepository;
    }
    
    public EventAnalyticsResponse getEventAnalytics(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", eventId));
        
        // Get all registrations for this event
        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        
        // Calculate registration metrics
        int totalRegistrations = registrations.size();
        long confirmedRegistrations = registrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED)
                .count();
        long cancelledRegistrations = registrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CANCELLED)
                .count();
        long pendingRegistrations = registrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.PENDING)
                .count();
        
        // Calculate ticket sales
        int totalTicketsSold = registrations.stream()
                .mapToInt(Registration::getQuantity)
                .sum();
        
        // Get total tickets available
        List<Ticket> tickets = ticketRepository.findByEventId(eventId);
        int totalTicketsAvailable = tickets.stream()
                .mapToInt(Ticket::getQuantity)
                .sum();
        
        // Calculate revenue from successful payments
        List<Payment> successfulPayments = paymentRepository.findAllByRegistration_EventIdAndStatus(
                eventId, PaymentStatus.SUCCESS);
        BigDecimal ticketSalesRevenue = successfulPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate total revenue (same as ticket sales revenue for now)
        BigDecimal totalRevenue = ticketSalesRevenue;
        
        // Calculate service costs
        List<EventServiceItem> services = eventServiceRepository.findByEventId(eventId);
        BigDecimal totalServiceCosts = services.stream()
                .map(EventServiceItem::getRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate profit and margin
        BigDecimal profit = totalRevenue.subtract(totalServiceCosts);
        BigDecimal margin = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            margin = profit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Calculate attendance metrics (assuming checked-in is tracked via registration status or separate check-in)
        // For now, we'll use confirmed registrations as a proxy
        int checkedInCount = (int) confirmedRegistrations; // This would come from check-in service
        int noShowCount = 0; // This would come from check-in service
        double attendanceRate = totalRegistrations > 0 ? 
                (double) checkedInCount / totalRegistrations * 100 : 0.0;
        
        // Generate registration trends (daily registrations for the last 30 days)
        List<Map<String, Object>> registrationTrends = generateRegistrationTrends(eventId, 30);
        
        return new EventAnalyticsResponse(
                eventId,
                event.getTitle(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                totalRegistrations,
                (int) confirmedRegistrations,
                (int) cancelledRegistrations,
                (int) pendingRegistrations,
                totalTicketsSold,
                totalTicketsAvailable,
                ticketSalesRevenue,
                totalRevenue,
                totalServiceCosts,
                profit,
                margin,
                checkedInCount,
                noShowCount,
                attendanceRate,
                registrationTrends
        );
    }
    
    public OrganizerDashboardResponse getOrganizerDashboard(UUID organizerId) {
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", organizerId));
        
        // Get all events by organizer
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        
        // Calculate event statistics
        int totalEvents = events.size();
        long activeEvents = events.stream()
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .count();
        long completedEvents = events.stream()
                .filter(e -> e.getStatus() == EventStatus.COMPLETED)
                .count();
        long cancelledEvents = events.stream()
                .filter(e -> e.getStatus() == EventStatus.CANCELLED)
                .count();
        
        // Calculate financial summary across all events
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalServiceCosts = BigDecimal.ZERO;
        int totalRegistrations = 0;
        int totalTicketsSold = 0;
        
        for (Event event : events) {
            List<Payment> successfulPayments = paymentRepository.findAllByRegistration_EventIdAndStatus(
                    event.getId(), PaymentStatus.SUCCESS);
            BigDecimal eventRevenue = successfulPayments.stream()
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalRevenue = totalRevenue.add(eventRevenue);
            
            List<EventServiceItem> services = eventServiceRepository.findByEventId(event.getId());
            BigDecimal eventServiceCosts = services.stream()
                    .map(EventServiceItem::getRate)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            totalServiceCosts = totalServiceCosts.add(eventServiceCosts);
            
            List<Registration> eventRegistrations = registrationRepository.findByEventId(event.getId());
            totalRegistrations += eventRegistrations.size();
            totalTicketsSold += eventRegistrations.stream()
                    .mapToInt(Registration::getQuantity)
                    .sum();
        }
        
        BigDecimal totalProfit = totalRevenue.subtract(totalServiceCosts);
        BigDecimal averageMargin = BigDecimal.ZERO;
        if (totalEvents > 0 && totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            averageMargin = totalProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Recent events performance (last 10 events)
        List<Map<String, Object>> recentEventsPerformance = events.stream()
                .sorted((e1, e2) -> e2.getCreatedAt().compareTo(e1.getCreatedAt()))
                .limit(10)
                .map(event -> {
                    Map<String, Object> performance = new HashMap<>();
                    performance.put("eventId", event.getId());
                    performance.put("title", event.getTitle());
                    performance.put("status", event.getStatus().toString());
                    
                    List<Payment> eventPayments = paymentRepository.findAllByRegistration_EventIdAndStatus(
                            event.getId(), PaymentStatus.SUCCESS);
                    BigDecimal revenue = eventPayments.stream()
                            .map(Payment::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    performance.put("revenue", revenue);
                    
                    List<Registration> eventRegs = registrationRepository.findByEventId(event.getId());
                    performance.put("registrations", eventRegs.size());
                    performance.put("ticketsSold", eventRegs.stream()
                            .mapToInt(Registration::getQuantity)
                            .sum());
                    
                    return performance;
                })
                .collect(Collectors.toList());
        
        // Revenue trends (monthly for last 12 months)
        List<Map<String, Object>> revenueTrends = generateRevenueTrends(organizerId, 12);
        
        return new OrganizerDashboardResponse(
                organizerId,
                organizer.getFirstName() + " " + organizer.getLastName(),
                totalEvents,
                (int) activeEvents,
                (int) completedEvents,
                (int) cancelledEvents,
                totalRevenue,
                totalServiceCosts,
                totalProfit,
                averageMargin,
                totalRegistrations,
                totalTicketsSold,
                recentEventsPerformance,
                revenueTrends
        );
    }
    
    public PlatformAnalyticsResponse getPlatformAnalytics() {
        // User statistics
        List<User> allUsers = userRepository.findAll();
        int totalUsers = allUsers.size();
        long totalOrganizers = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.ORGANIZER)
                .count();
        long totalAttendees = allUsers.stream()
                .filter(u -> u.getRole() == UserRole.ATTENDEE)
                .count();
        long activeUsers = allUsers.stream()
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .count();
        
        // Event statistics
        List<Event> allEvents = eventRepository.findAll();
        int totalEvents = allEvents.size();
        long publishedEvents = allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED)
                .count();
        long completedEvents = allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.COMPLETED)
                .count();
        long upcomingEvents = allEvents.stream()
                .filter(e -> e.getStatus() == EventStatus.PUBLISHED && 
                        e.getStartDateTime().isAfter(LocalDateTime.now()))
                .count();
        
        // Financial statistics
        List<Payment> allPayments = paymentRepository.findAll();
        BigDecimal totalPlatformRevenue = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPaymentsProcessed = totalPlatformRevenue;
        BigDecimal totalRefunds = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.REFUNDED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Platform commission (assuming 10% commission)
        BigDecimal platformCommission = totalPlatformRevenue.multiply(BigDecimal.valueOf(0.10))
                .setScale(2, RoundingMode.HALF_UP);
        
        // Registration statistics
        List<Registration> allRegistrations = registrationRepository.findAll();
        int totalRegistrations = allRegistrations.size();
        int totalTicketsSold = allRegistrations.stream()
                .mapToInt(Registration::getQuantity)
                .sum();
        
        double averageEventAttendance = totalEvents > 0 ? 
                (double) totalRegistrations / totalEvents : 0.0;
        
        // Events by category
        List<Map<String, Object>> eventsByCategory = generateEventsByCategory();
        
        // Platform revenue trends (monthly for last 12 months)
        List<Map<String, Object>> platformRevenueTrends = generatePlatformRevenueTrends(12);
        
        return new PlatformAnalyticsResponse(
                totalUsers,
                (int) totalOrganizers,
                (int) totalAttendees,
                (int) activeUsers,
                totalEvents,
                (int) publishedEvents,
                (int) completedEvents,
                (int) upcomingEvents,
                totalPlatformRevenue,
                totalPaymentsProcessed,
                totalRefunds,
                platformCommission,
                totalRegistrations,
                totalTicketsSold,
                averageEventAttendance,
                eventsByCategory,
                platformRevenueTrends
        );
    }
    
    private List<Map<String, Object>> generateRegistrationTrends(UUID eventId, int days) {
        List<Map<String, Object>> trends = new ArrayList<>();
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);
        
        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        
        Map<String, Long> dailyCounts = registrations.stream()
                .filter(r -> r.getRegisteredAt() != null && 
                        r.getRegisteredAt().isAfter(startDate) && 
                        r.getRegisteredAt().isBefore(endDate))
                .collect(Collectors.groupingBy(
                        r -> r.getRegisteredAt().toLocalDate().toString(),
                        Collectors.counting()
                ));
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = endDate.minusDays(i);
            String dateKey = date.toLocalDate().toString();
            Long count = dailyCounts.getOrDefault(dateKey, 0L);
            
            Map<String, Object> trend = new HashMap<>();
            trend.put("date", dateKey);
            trend.put("count", count);
            trends.add(trend);
        }
        
        return trends;
    }
    
    private List<Map<String, Object>> generateRevenueTrends(UUID organizerId, int months) {
        List<Map<String, Object>> trends = new ArrayList<>();
        List<Event> events = eventRepository.findByOrganizerId(organizerId);
        
        Map<String, BigDecimal> monthlyRevenue = new HashMap<>();
        
        for (Event event : events) {
            List<Payment> payments = paymentRepository.findAllByRegistration_EventIdAndStatus(
                    event.getId(), PaymentStatus.SUCCESS);
            
            for (Payment payment : payments) {
                if (payment.getPaidAt() != null) {
                    String monthKey = payment.getPaidAt().toLocalDate()
                            .withDayOfMonth(1)
                            .toString();
                    monthlyRevenue.merge(monthKey, payment.getAmount(), BigDecimal::add);
                }
            }
        }
        
        LocalDateTime now = LocalDateTime.now();
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime month = now.minusMonths(i).withDayOfMonth(1);
            String monthKey = month.toLocalDate().toString();
            
            Map<String, Object> trend = new HashMap<>();
            trend.put("month", monthKey);
            trend.put("revenue", monthlyRevenue.getOrDefault(monthKey, BigDecimal.ZERO));
            trends.add(trend);
        }
        
        return trends;
    }
    
    private List<Map<String, Object>> generateEventsByCategory() {
        List<Event> events = eventRepository.findAll();
        Map<UUID, Long> categoryCounts = events.stream()
                .filter(e -> e.getCategoryId() != null)
                .collect(Collectors.groupingBy(
                        Event::getCategoryId,
                        Collectors.counting()
                ));
        
        return categoryCounts.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> categoryData = new HashMap<>();
                    categoryRepository.findById(entry.getKey()).ifPresent(category -> {
                        categoryData.put("categoryId", entry.getKey());
                        categoryData.put("categoryName", category.getName());
                        categoryData.put("eventCount", entry.getValue());
                    });
                    return categoryData;
                })
                .filter(m -> !m.isEmpty())
                .collect(Collectors.toList());
    }
    
    private List<Map<String, Object>> generatePlatformRevenueTrends(int months) {
        List<Map<String, Object>> trends = new ArrayList<>();
        List<Payment> allPayments = paymentRepository.findAll();
        
        Map<String, BigDecimal> monthlyRevenue = allPayments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.SUCCESS && p.getPaidAt() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getPaidAt().toLocalDate().withDayOfMonth(1).toString(),
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));
        
        LocalDateTime now = LocalDateTime.now();
        for (int i = months - 1; i >= 0; i--) {
            LocalDateTime month = now.minusMonths(i).withDayOfMonth(1);
            String monthKey = month.toLocalDate().toString();
            
            Map<String, Object> trend = new HashMap<>();
            trend.put("month", monthKey);
            trend.put("revenue", monthlyRevenue.getOrDefault(monthKey, BigDecimal.ZERO));
            trends.add(trend);
        }
        
        return trends;
    }
}





