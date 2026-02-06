package za.co.rubhub.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import za.co.rubhub.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "support_tickets")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "ticket_number", nullable = false, unique = true, length = 50)
    private String ticketNumber;
    
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    
    @Column(name = "assigned_to", length = 100)
    private String assignedTo;
    
    @Column(name = "subject", nullable = false, length = 200)
    private String subject;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "category", nullable = false, length = 50)
    private String category; // billing, technical, service, safety, general, account, payment
    
    @Column(name = "sub_category", length = 50)
    private String subCategory; // e.g., "refund", "password_reset", "booking_issue"
    
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private String priority = "medium"; // low, medium, high, urgent
    
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "open"; // open, in-progress, resolved, closed, pending, escalated
    
    @Column(name = "related_request", length = 100)
    private String relatedRequest;
    
    @Column(name = "related_booking_id")
    private Long relatedBookingId;
    
    @Column(name = "related_user_id")
    private String relatedUserId;
    
    @ElementCollection
    @CollectionTable(name = "support_ticket_messages", 
                     joinColumns = @JoinColumn(name = "ticket_id"))
    @Builder.Default
    private List<TicketMessage> messages = new ArrayList<>();
    
    @Embedded
    private Resolution resolution;
    
    // Additional fields
    @Column(name = "source", length = 50)
    private String source; // web, mobile_app, email, phone, chat
    
    @Column(name = "channel", length = 50)
    private String channel; // help_center, email, live_chat, phone
    
    @Column(name = "tags", columnDefinition = "jsonb")
    private String tags; // JSON array of tags
    
    @Column(name = "attachments", columnDefinition = "jsonb")
    private String attachments; // JSON array of attachment URLs
    
    @Column(name = "customer_email", length = 100)
    private String customerEmail;
    
    @Column(name = "customer_phone", length = 20)
    private String customerPhone;
    
    @Column(name = "customer_name", length = 100)
    private String customerName;
    
    @Column(name = "first_response_time_minutes")
    private Integer firstResponseTimeMinutes;
    
    @Column(name = "first_response_at")
    private LocalDateTime firstResponseAt;
    
    @Column(name = "resolution_time_minutes")
    private Integer resolutionTimeMinutes;
    
    @Column(name = "satisfaction_score")
    private Integer satisfactionScore; // 1-5
    
    @Column(name = "satisfaction_feedback", columnDefinition = "TEXT")
    private String satisfactionFeedback;
    
    @Column(name = "is_escalated")
    @Builder.Default
    private Boolean isEscalated = false;
    
    @Column(name = "escalated_to", length = 100)
    private String escalatedTo;
    
    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;
    
    @Column(name = "escalation_reason", length = 500)
    private String escalationReason;
    
    @Column(name = "sla_breached")
    @Builder.Default
    private Boolean slaBreached = false;
    
    @Column(name = "sla_breach_reason", length = 500)
    private String slaBreachReason;
    
    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = false;
    
    @Column(name = "follow_up_date")
    private LocalDateTime followUpDate;
    
    @Column(name = "internal_notes", columnDefinition = "TEXT")
    private String internalNotes;
    
    @Column(name = "cc_emails", columnDefinition = "jsonb")
    private String ccEmails; // JSON array of email addresses
    
    @Column(name = "bcc_emails", columnDefinition = "jsonb")
    private String bccEmails; // JSON array of email addresses
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    
    @Column(name = "last_reply_at")
    private LocalDateTime lastReplyAt;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    // Embeddable classes
    @Embeddable
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TicketMessage {
        
        @Column(name = "sender", nullable = false, length = 100)
        private String sender;
        
        @Column(name = "sender_email", length = 100)
        private String senderEmail;
        
        @Column(name = "sender_type", length = 20)
        private String senderType; // customer, agent, system
        
        @Column(name = "content", nullable = false, columnDefinition = "TEXT")
        private String content;
        
        @Column(name = "is_internal")
        @Builder.Default
        private Boolean isInternal = false;
        
        @Column(name = "sent_at", nullable = false)
        private LocalDateTime sentAt;
        
        @Column(name = "read_at")
        private LocalDateTime readAt;
        
        @Column(name = "read_by", length = 100)
        private String readBy;
        
        @Column(name = "attachments", columnDefinition = "jsonb")
        private String attachments; // JSON array of attachment URLs
        
        @Column(name = "message_type", length = 20)
        private String messageType; // text, email, system_notification
        
        @Column(name = "is_auto_reply")
        @Builder.Default
        private Boolean isAutoReply = false;
        
        @Column(name = "in_reply_to")
        private Long inReplyTo; // reference to previous message ID
    }
    
    @Embeddable
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Resolution {
        
        @Column(name = "resolution_notes", columnDefinition = "TEXT")
        private String notes;
        
        @Column(name = "resolved_by", length = 100)
        private String resolvedBy;
        
        @Column(name = "resolved_at")
        private LocalDateTime resolvedAt;
        
        @Column(name = "resolution_type", length = 50)
        private String resolutionType; // fixed, workaround, duplicate, not_a_bug, cannot_reproduce
        
        @Column(name = "resolution_category", length = 50)
        private String resolutionCategory; // technical, billing, service, other
        
        @Column(name = "follow_up_action", length = 500)
        private String followUpAction;
        
        @Column(name = "root_cause", length = 500)
        private String rootCause;
        
        @Column(name = "preventive_measures", columnDefinition = "TEXT")
        private String preventiveMeasures;
    }
    
    // Business logic methods
    @PrePersist
    protected void onCreate() {
        if (ticketNumber == null) {
            ticketNumber = generateTicketNumber();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Update last reply date if new messages added
        if (messages != null && !messages.isEmpty()) {
            lastReplyAt = messages.stream()
                .map(TicketMessage::getSentAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        }
        
        // Calculate resolution time if resolved
        if ("resolved".equals(status) || "closed".equals(status)) {
            if (resolution != null && resolution.getResolvedAt() != null && createdAt != null) {
                long minutes = java.time.Duration.between(createdAt, resolution.getResolvedAt()).toMinutes();
                resolutionTimeMinutes = (int) minutes;
            }
            if (closedAt == null) {
                closedAt = LocalDateTime.now();
            }
        }
        
        // Calculate first response time
        if (firstResponseAt != null && createdAt != null && firstResponseTimeMinutes == null) {
            long minutes = java.time.Duration.between(createdAt, firstResponseAt).toMinutes();
            firstResponseTimeMinutes = (int) minutes;
        }
    }
    
    private String generateTicketNumber() {
        return "TICKET-" + System.currentTimeMillis() + "-" + 
               (int)(Math.random() * 1000);
    }
    
    public void addMessage(TicketMessage message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        
        // Update first response time if this is the first agent response
        if (firstResponseAt == null && 
            ("agent".equals(message.getSenderType()) || 
             !"customer".equals(message.getSenderType()))) {
            firstResponseAt = message.getSentAt();
        }
    }
    
    public void resolveTicket(String resolvedByUser, String notes, String resolutionType) {
        this.status = "resolved";
        this.resolution = Resolution.builder()
            .notes(notes)
            .resolvedBy(resolvedByUser)
            .resolvedAt(LocalDateTime.now())
            .resolutionType(resolutionType)
            .build();
    }
    
    public void closeTicket() {
        this.status = "closed";
        if (this.resolution != null && this.resolution.getResolvedAt() == null) {
            this.resolution.setResolvedAt(LocalDateTime.now());
        }
        this.closedAt = LocalDateTime.now();
    }
    
    public void escalateTicket(String escalatedToDepartment, String reason) {
        this.isEscalated = true;
        this.escalatedTo = escalatedToDepartment;
        this.escalatedAt = LocalDateTime.now();
        this.escalationReason = reason;
        this.status = "escalated";
    }
    
    public boolean isOpen() {
        return "open".equals(status) || "in-progress".equals(status);
    }
    
    public boolean isResolved() {
        return "resolved".equals(status) || "closed".equals(status);
    }
    
    public boolean isOverdue() {
        if (dueDate == null) return false;
        return LocalDateTime.now().isAfter(dueDate) && isOpen();
    }
    
    public String getFormattedPriority() {
        switch (priority.toLowerCase()) {
            case "urgent": return "🔴 Urgent";
            case "high": return "🟠 High";
            case "medium": return "🟡 Medium";
            case "low": return "🟢 Low";
            default: return priority;
        }
    }
    
    public String getFormattedStatus() {
        switch (status.toLowerCase()) {
            case "open": return "📝 Open";
            case "in-progress": return "🔄 In Progress";
            case "resolved": return "✅ Resolved";
            case "closed": return "🔒 Closed";
            case "escalated": return "🚨 Escalated";
            case "pending": return "⏳ Pending";
            default: return status;
        }
    }
    
    // Indexes for better query performance
    @Table(name = "support_tickets", indexes = {
        @Index(name = "idx_support_ticket_number", columnList = "ticket_number", unique = true),
        @Index(name = "idx_support_ticket_status", columnList = "status"),
        @Index(name = "idx_support_ticket_priority", columnList = "priority"),
        @Index(name = "idx_support_ticket_category", columnList = "category"),
        @Index(name = "idx_support_ticket_created_by", columnList = "created_by"),
        @Index(name = "idx_support_ticket_assigned_to", columnList = "assigned_to"),
        @Index(name = "idx_support_ticket_created_at", columnList = "created_at"),
        @Index(name = "idx_support_ticket_updated_at", columnList = "updated_at"),
        @Index(name = "idx_support_ticket_related_booking", columnList = "related_booking_id"),
        @Index(name = "idx_support_ticket_customer_email", columnList = "customer_email"),
        @Index(name = "idx_support_ticket_is_escalated", columnList = "is_escalated"),
        @Index(name = "idx_support_ticket_status_priority", columnList = "status, priority"),
        @Index(name = "idx_support_ticket_category_status", columnList = "category, status")
    })
    static class SupportTicketTableIndices {}
}