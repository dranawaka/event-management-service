package com.aurelius.tech.eventmanagementservice.service;

import com.aurelius.tech.eventmanagementservice.dto.request.CreatePayoutRequest;
import com.aurelius.tech.eventmanagementservice.dto.response.InvoiceResponse;
import com.aurelius.tech.eventmanagementservice.entity.*;
import com.aurelius.tech.eventmanagementservice.entity.enums.PayoutStatus;
import com.aurelius.tech.eventmanagementservice.exception.BusinessException;
import com.aurelius.tech.eventmanagementservice.exception.ResourceNotFoundException;
import com.aurelius.tech.eventmanagementservice.repository.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class BillingService {
    
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final RegistrationRepository registrationRepository;
    private final PayoutRepository payoutRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    
    @Value("${app.invoice.storage-path:./invoices}")
    private String invoiceStoragePath;
    
    public BillingService(InvoiceRepository invoiceRepository,
                         PaymentRepository paymentRepository,
                         RegistrationRepository registrationRepository,
                         PayoutRepository payoutRepository,
                         UserRepository userRepository,
                         EventRepository eventRepository,
                         TicketRepository ticketRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
        this.registrationRepository = registrationRepository;
        this.payoutRepository = payoutRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }
    
    @Transactional
    public Invoice generateInvoice(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
        
        if (payment.getStatus() != com.aurelius.tech.eventmanagementservice.entity.enums.PaymentStatus.SUCCESS) {
            throw new BusinessException("Invoice can only be generated for successful payments");
        }
        
        // Check if invoice already exists
        invoiceRepository.findByPaymentId(paymentId)
                .ifPresent(invoice -> {
                    throw new BusinessException("Invoice already exists for this payment");
                });
        
        // Fetch related entities
        Registration registration = registrationRepository.findById(payment.getRegistrationId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "id", payment.getRegistrationId()));
        
        Event event = eventRepository.findById(registration.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", "id", registration.getEventId()));
        
        User user = userRepository.findById(registration.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", registration.getUserId()));
        
        Ticket ticket = null;
        if (registration.getTicketId() != null) {
            ticket = ticketRepository.findById(registration.getTicketId()).orElse(null);
        }
        
        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber();
        
        // Create invoice entity
        Invoice invoice = new Invoice();
        invoice.setPaymentId(paymentId);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setAmount(payment.getAmount());
        invoice.setCurrency(payment.getCurrency());
        invoice.setIssuedAt(LocalDateTime.now());
        
        invoice = invoiceRepository.save(invoice);
        
        // Generate PDF invoice
        try {
            String filePath = generateInvoicePDF(invoice, payment, registration, event, user, ticket);
            invoice.setFilePath(filePath);
            invoice = invoiceRepository.save(invoice);
        } catch (IOException e) {
            throw new BusinessException("Failed to generate invoice PDF: " + e.getMessage());
        }
        
        return invoice;
    }
    
    public InvoiceResponse getInvoiceByPaymentId(UUID paymentId) {
        Invoice invoice = invoiceRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "paymentId", paymentId));
        
        return mapToInvoiceResponse(invoice);
    }
    
    public InvoiceResponse getInvoiceByInvoiceNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "invoiceNumber", invoiceNumber));
        
        return mapToInvoiceResponse(invoice);
    }
    
    public byte[] getInvoicePDF(UUID invoiceId) throws IOException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        
        if (invoice.getFilePath() == null) {
            throw new BusinessException("Invoice PDF not found");
        }
        
        Path filePath = Paths.get(invoice.getFilePath());
        if (!Files.exists(filePath)) {
            throw new BusinessException("Invoice PDF file not found on disk");
        }
        
        return Files.readAllBytes(filePath);
    }
    
    @Transactional
    public Payout createPayout(CreatePayoutRequest request) {
        userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getOrganizerId()));
        
        if (request.getEventId() != null) {
            eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event", "id", request.getEventId()));
        }
        
        Payout payout = new Payout();
        payout.setOrganizerId(request.getOrganizerId());
        payout.setEventId(request.getEventId());
        payout.setAmount(request.getAmount());
        payout.setCurrency(request.getCurrency() != null ? request.getCurrency() : "USD");
        payout.setStatus(PayoutStatus.PENDING);
        payout.setPaymentMethod(request.getPaymentMethod());
        payout.setNotes(request.getNotes());
        
        return payoutRepository.save(payout);
    }
    
    @Transactional
    public Payout processPayout(UUID payoutId, String transactionReference) {
        Payout payout = payoutRepository.findById(payoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Payout", "id", payoutId));
        
        if (payout.getStatus() != PayoutStatus.PENDING) {
            throw new BusinessException("Only pending payouts can be processed");
        }
        
        payout.setStatus(PayoutStatus.PROCESSING);
        payout.setTransactionReference(transactionReference);
        payout = payoutRepository.save(payout);
        
        // In a real implementation, this would integrate with payment gateway
        // For now, we'll mark it as completed
        payout.setStatus(PayoutStatus.COMPLETED);
        payout.setProcessedAt(LocalDateTime.now());
        
        return payoutRepository.save(payout);
    }
    
    public List<Payout> getPayoutsByOrganizer(UUID organizerId) {
        return payoutRepository.findByOrganizerId(organizerId);
    }
    
    public List<Payout> getPayoutsByEvent(UUID eventId) {
        return payoutRepository.findByEventId(eventId);
    }
    
    private String generateInvoiceNumber() {
        String prefix = "INV";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("%s-%s-%s", prefix, timestamp, random);
    }
    
    private String generateInvoicePDF(Invoice invoice, Payment payment, Registration registration,
                                      Event event, User user, Ticket ticket) throws IOException {
        // Create invoice directory if it doesn't exist
        Path invoiceDir = Paths.get(invoiceStoragePath);
        if (!Files.exists(invoiceDir)) {
            Files.createDirectories(invoiceDir);
        }
        
        String fileName = String.format("invoice_%s.pdf", invoice.getInvoiceNumber());
        Path filePath = invoiceDir.resolve(fileName);
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float yPosition = 750;
                float margin = 50;
                float lineHeight = 20;
                
                // Header
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 24);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("INVOICE");
                contentStream.endText();
                
                yPosition -= 40;
                
                // Invoice details
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Invoice Number: " + invoice.getInvoiceNumber());
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Date: " + invoice.getIssuedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                contentStream.endText();
                
                yPosition -= 40;
                
                // Customer information
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Bill To:");
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(user.getFirstName() + " " + user.getLastName());
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(user.getEmail());
                contentStream.endText();
                
                yPosition -= 40;
                
                // Event information
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Event Details:");
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Event: " + event.getTitle());
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Date: " + event.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                contentStream.endText();
                
                yPosition -= 40;
                
                // Line items
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Description");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(400, yPosition);
                contentStream.showText("Quantity");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(480, yPosition);
                contentStream.showText("Amount");
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                
                String description = ticket != null ? ticket.getName() : "Event Registration";
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(description);
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(400, yPosition);
                contentStream.showText(String.valueOf(registration.getQuantity()));
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(480, yPosition);
                contentStream.showText("$" + payment.getAmount().toString());
                contentStream.endText();
                
                yPosition -= 40;
                
                // Total
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(400, yPosition);
                contentStream.showText("Total: $" + payment.getAmount().toString());
                contentStream.endText();
                
                yPosition -= 40;
                
                // Payment information
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Payment Method: " + (payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "N/A"));
                contentStream.endText();
                
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Transaction ID: " + (payment.getTransactionId() != null ? payment.getTransactionId() : "N/A"));
                contentStream.endText();
            }
            
            document.save(filePath.toFile());
        }
        
        return filePath.toAbsolutePath().toString();
    }
    
    private InvoiceResponse mapToInvoiceResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getPaymentId(),
                invoice.getInvoiceNumber(),
                invoice.getAmount(),
                invoice.getCurrency(),
                invoice.getFilePath(),
                invoice.getIssuedAt(),
                invoice.getCreatedAt()
        );
    }
}

