package com.aurelius.tech.eventmanagementservice.controller;

import com.aurelius.tech.eventmanagementservice.dto.request.CreatePayoutRequest;
import com.aurelius.tech.eventmanagementservice.dto.response.InvoiceResponse;
import com.aurelius.tech.eventmanagementservice.entity.Payout;
import com.aurelius.tech.eventmanagementservice.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/billing")
public class BillingController {
    
    private final BillingService billingService;
    
    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }
    
    @PostMapping("/invoices/generate")
    public ResponseEntity<InvoiceResponse> generateInvoice(@RequestBody Map<String, String> request) {
        UUID paymentId = UUID.fromString(request.get("paymentId"));
        return ResponseEntity.ok(billingService.getInvoiceByPaymentId(
                billingService.generateInvoice(paymentId).getPaymentId()));
    }
    
    @GetMapping("/invoices/payment/{paymentId}")
    public ResponseEntity<InvoiceResponse> getInvoiceByPaymentId(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(billingService.getInvoiceByPaymentId(paymentId));
    }
    
    @GetMapping("/invoices/{invoiceNumber}")
    public ResponseEntity<InvoiceResponse> getInvoiceByInvoiceNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(billingService.getInvoiceByInvoiceNumber(invoiceNumber));
    }
    
    @GetMapping("/invoices/{id}/pdf")
    public ResponseEntity<byte[]> getInvoicePDF(@PathVariable UUID id) throws IOException {
        byte[] pdfData = billingService.getInvoicePDF(id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"invoice.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }
    
    @PostMapping("/payouts")
    public ResponseEntity<Payout> createPayout(@Valid @RequestBody CreatePayoutRequest request) {
        return ResponseEntity.ok(billingService.createPayout(request));
    }
    
    @PostMapping("/payouts/{id}/process")
    public ResponseEntity<Payout> processPayout(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        String transactionReference = request.get("transactionReference");
        return ResponseEntity.ok(billingService.processPayout(id, transactionReference));
    }
    
    @GetMapping("/payouts/organizer/{organizerId}")
    public ResponseEntity<List<Payout>> getPayoutsByOrganizer(@PathVariable UUID organizerId) {
        return ResponseEntity.ok(billingService.getPayoutsByOrganizer(organizerId));
    }
    
    @GetMapping("/payouts/event/{eventId}")
    public ResponseEntity<List<Payout>> getPayoutsByEvent(@PathVariable UUID eventId) {
        return ResponseEntity.ok(billingService.getPayoutsByEvent(eventId));
    }
}





