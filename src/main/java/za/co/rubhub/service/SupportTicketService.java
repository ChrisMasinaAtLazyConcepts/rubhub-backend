package za.co.rubhub.service;
import za.co.rubhub.model.SupportTicket;
import za.co.rubhub.repositories.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportTicketService {
    private final SupportTicketRepository supportTicketRepository;

    public SupportTicket createTicket(SupportTicket ticket) {
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());
        return supportTicketRepository.save(ticket);
    }

    public List<SupportTicket> getAllTickets() {
        return supportTicketRepository.findAll();
    }

    public Optional<SupportTicket> getTicketById(Long id) {
        return supportTicketRepository.findById(id);
    }

    public Optional<SupportTicket> getTicketByNumber(String ticketNumber) {
        return supportTicketRepository.findByTicketNumber(ticketNumber);
    }

    public SupportTicket updateTicketStatus(Long id, String status, String assignedTo) {
        return supportTicketRepository.findById(id).map(ticket -> {
            ticket.setStatus(status);
            if (assignedTo != null) {
                ticket.setAssignedTo(assignedTo);
            }
            ticket.setUpdatedAt(LocalDateTime.now());
            return supportTicketRepository.save(ticket);
        }).orElseThrow(() -> new RuntimeException("Support ticket not found"));
    }

    public SupportTicket addMessage(Long ticketId, SupportTicket.TicketMessage message) {
        return supportTicketRepository.findById(ticketId).map(ticket -> {
            message.setSentAt(LocalDateTime.now());
            ticket.getMessages().add(message);
            ticket.setUpdatedAt(LocalDateTime.now());
            return supportTicketRepository.save(ticket);
        }).orElseThrow(() -> new RuntimeException("Support ticket not found"));
    }

    public SupportTicket resolveTicket(Long ticketId, SupportTicket.Resolution resolution) {
        return supportTicketRepository.findById(ticketId).map(ticket -> {
            resolution.setResolvedAt(LocalDateTime.now());
            ticket.setResolution(resolution);
            ticket.setStatus("resolved");
            ticket.setUpdatedAt(LocalDateTime.now());
            return supportTicketRepository.save(ticket);
        }).orElseThrow(() -> new RuntimeException("Support ticket not found"));
    }
}