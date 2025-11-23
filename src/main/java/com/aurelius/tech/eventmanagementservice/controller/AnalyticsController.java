package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.dto.response.EventAnalyticsResponse;
import com.aurelius.tech.eventmanagementservice.dto.response.OrganizerDashboardResponse;
import com.aurelius.tech.eventmanagementservice.dto.response.PlatformAnalyticsResponse;
import com.aurelius.tech.eventmanagementservice.service.AnalyticsService;
import com.aurelius.tech.eventmanagementservice.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    private final ReportService reportService;
    
    public AnalyticsController(AnalyticsService analyticsService, ReportService reportService) {
        this.analyticsService = analyticsService;
        this.reportService = reportService;
    }
    
    @GetMapping("/events/{id}")
    public ResponseEntity<EventAnalyticsResponse> getEventAnalytics(@PathVariable UUID id) {
        return ResponseEntity.ok(analyticsService.getEventAnalytics(id));
    }
    
    @GetMapping("/organizer/{id}")
    public ResponseEntity<OrganizerDashboardResponse> getOrganizerDashboard(@PathVariable UUID id) {
        return ResponseEntity.ok(analyticsService.getOrganizerDashboard(id));
    }
    
    @GetMapping("/platform")
    public ResponseEntity<PlatformAnalyticsResponse> getPlatformAnalytics() {
        return ResponseEntity.ok(analyticsService.getPlatformAnalytics());
    }
    
    @GetMapping("/reports/export")
    public ResponseEntity<byte[]> exportReport(
            @RequestParam UUID eventId,
            @RequestParam(defaultValue = "csv") String format) throws IOException {
        
        byte[] reportData;
        String contentType;
        String fileName;
        
        if ("pdf".equalsIgnoreCase(format)) {
            reportData = reportService.exportEventReportPDF(eventId);
            contentType = MediaType.APPLICATION_PDF_VALUE;
            fileName = "event_report_" + eventId + ".pdf";
        } else {
            reportData = reportService.exportEventRegistrationsCSV(eventId);
            contentType = "text/csv";
            fileName = "event_registrations_" + eventId + ".csv";
        }
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(reportData);
    }
    
    @GetMapping("/reports/payments/export")
    public ResponseEntity<byte[]> exportPaymentsReport(@RequestParam UUID eventId) throws IOException {
        byte[] reportData = reportService.exportEventPaymentsCSV(eventId);
        String fileName = "event_payments_" + eventId + ".csv";
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(reportData);
    }
}

