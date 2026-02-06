package za.co.rubhub.controller;

import za.co.rubhub.model.SupportTicket;
import za.co.rubhub.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/support-tickets")
@RequiredArgsConstructor
public class SupportTicketController {
    private final SupportTicketService supportTicketService;

    @PostMapping
    public ResponseEntity<SupportTicket> createTicket(@RequestBody SupportTicket ticket) {
        return ResponseEntity.ok(supportTicketService.createTicket(ticket));
    }

    @GetMapping
    public ResponseEntity<List<SupportTicket>> getAllTickets() {
        return ResponseEntity.ok(supportTicketService.getAllTickets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportTicket> getTicketById(@PathVariable Long id) {
        return supportTicketService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<SupportTicket> getTicketByNumber(@PathVariable String ticketNumber) {
        return supportTicketService.getTicketByNumber(ticketNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SupportTicket> updateTicketStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String assignedTo) {
        return ResponseEntity.ok(supportTicketService.updateTicketStatus(id, status, assignedTo));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<SupportTicket> addMessage(
            @PathVariable Long id,
            @RequestBody SupportTicket.TicketMessage message) {
        return ResponseEntity.ok(supportTicketService.addMessage(id, message));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<SupportTicket> resolveTicket(
            @PathVariable Long id,
            @RequestBody SupportTicket.Resolution resolution) {
        return ResponseEntity.ok(supportTicketService.resolveTicket(id, resolution));
    }
}