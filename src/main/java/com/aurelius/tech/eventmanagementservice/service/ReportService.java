package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.entity.*;
import com.aurelius.tech.eventmanagementservice.entity.enums.PaymentStatus;
import com.aurelius.tech.eventmanagementservice.entity.enums.RegistrationStatus;
import com.aurelius.tech.eventmanagementservice.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {
    
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final PaymentRepository paymentRepository;
    
    public ReportService(EventRepository eventRepository,
                        RegistrationRepository registrationRepository,
                        PaymentRepository paymentRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.paymentRepository = paymentRepository;
    }
    
    public byte[] exportEventRegistrationsCSV(UUID eventId) throws IOException {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        
        StringWriter writer = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(
                "Registration ID", "User Name", "Email", "Ticket Type", 
                "Quantity", "Total Amount", "Status", "Registered At"
        );
        
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (Registration registration : registrations) {
                String userName = registration.getUser() != null ? 
                        registration.getUser().getFirstName() + " " + registration.getUser().getLastName() : "N/A";
                String email = registration.getUser() != null ? 
                        registration.getUser().getEmail() : "N/A";
                String ticketName = registration.getTicket() != null ? 
                        registration.getTicket().getName() : "N/A";
                
                csvPrinter.printRecord(
                        registration.getId(),
                        userName,
                        email,
                        ticketName,
                        registration.getQuantity(),
                        registration.getTotalAmount() != null ? registration.getTotalAmount().toString() : "0.00",
                        registration.getStatus().toString(),
                        registration.getRegisteredAt() != null ? 
                                registration.getRegisteredAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "N/A"
                );
            }
        }
        
        return writer.toString().getBytes();
    }
    
    public byte[] exportEventPaymentsCSV(UUID eventId) throws IOException {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        List<Payment> payments = paymentRepository.findAllByRegistration_EventIdAndStatus(
                eventId, PaymentStatus.SUCCESS);
        
        StringWriter writer = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader(
                "Payment ID", "Registration ID", "Amount", "Currency", 
                "Payment Method", "Transaction ID", "Status", "Paid At"
        );
        
        try (CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
            for (Payment payment : payments) {
                csvPrinter.printRecord(
                        payment.getId(),
                        payment.getRegistrationId(),
                        payment.getAmount().toString(),
                        payment.getCurrency(),
                        payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "N/A",
                        payment.getTransactionId() != null ? payment.getTransactionId() : "N/A",
                        payment.getStatus().toString(),
                        payment.getPaidAt() != null ? 
                                payment.getPaidAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "N/A"
                );
            }
        }
        
        return writer.toString().getBytes();
    }
    
    public byte[] exportEventReportPDF(UUID eventId) throws IOException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        List<Payment> payments = paymentRepository.findAllByRegistration_EventIdAndStatus(
                eventId, PaymentStatus.SUCCESS);
        
        BigDecimal totalRevenue = payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalRegistrations = registrations.size();
        int confirmedRegistrations = (int) registrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED)
                .count();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = 750;
                float margin = 50;
                float lineHeight = 20;
                
                // Title
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 20);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Event Report: " + event.getTitle());
                contentStream.endText();
                
                yPosition -= 40;
                
                // Event Details
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Event Details");
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Start Date: " + event.getStartDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("End Date: " + event.getEndDateTime()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Status: " + event.getStatus().toString());
                contentStream.endText();
                
                yPosition -= 40;
                
                // Statistics
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Statistics");
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Total Registrations: " + totalRegistrations);
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Confirmed Registrations: " + confirmedRegistrations);
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Total Revenue: $" + totalRevenue.toString());
                contentStream.endText();
                
                yPosition -= 40;
                
                // Generated date
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Report Generated: " + LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                contentStream.endText();
            }
            
            document.save(baos);
        }
        
        return baos.toByteArray();
    }
}

