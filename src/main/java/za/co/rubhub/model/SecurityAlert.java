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
@Table(name = "security_alerts")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 50)
    private AlertType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AlertStatus status = AlertStatus.ACTIVE;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking request;
    
    @Embedded
    private Location location;
    
    @Embedded
    private NotificationStatus notifications;
    
    @Embedded
    private SecurityCompany securityCompany;
    
    @Column(name = "stream_available")
    @Builder.Default
    private Boolean streamAvailable = false;
    
    @Column(name = "is_flashing")
    @Builder.Default
    private Boolean isFlashing = false;
    
    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 1; // 1-5, 5 being highest
    
    // Evidence and recording
    @Column(name = "recording_url", length = 500)
    private String recordingUrl;
    
    @Column(name = "recording_started_at")
    private LocalDateTime recordingStartedAt;
    
    @Column(name = "recording_ended_at")
    private LocalDateTime recordingEndedAt;
    
    @Column(name = "recording_duration")
    private Long recordingDuration; // in seconds
    
    // Resolution details
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @Column(name = "resolved_by", length = 100)
    private String resolvedBy;
    
    @ElementCollection
    @CollectionTable(name = "security_alert_actions", 
                     joinColumns = @JoinColumn(name = "alert_id"))
    @Builder.Default
    private List<ActionTaken> actionsTaken = new ArrayList<>();
    
    // Additional fields
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "severity_level", length = 20)
    private String severityLevel; // low, medium, high, critical
    
    @Column(name = "incident_category", length = 50)
    private String incidentCategory; // safety, medical, theft, harassment, etc.
    
    @Column(name = "is_false_alarm")
    @Builder.Default
    private Boolean isFalseAlarm = false;
    
    @Column(name = "false_alarm_reason", length = 500)
    private String falseAlarmReason;
    
    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;
    
    @Column(name = "acknowledged_by", length = 100)
    private String acknowledgedBy;
    
    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;
    
    @Column(name = "escalated_to", length = 100)
    private String escalatedTo;
    
    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = false;
    
    @Column(name = "follow_up_notes", columnDefinition = "TEXT")
    private String followUpNotes;
    
    @Column(name = "resolution_time_minutes")
    private Integer resolutionTimeMinutes;
    
    @Column(name = "response_time_minutes")
    private Integer responseTimeMinutes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum AlertType {
        PANIC_BUTTON,
        SELFIE_VERIFICATION_FAILED,
        LOCATION_ANOMALY,
        DURATION_OVERRUN,
        NO_SHOW,
        CUSTOMER_COMPLAINT,
        THERAPIST_COMPLAINT,
        ASSAULT,
        THEFT,
        HARASSMENT,
        MEDICAL_EMERGENCY,
        SYSTEM_ALERT
    }
    
    public enum AlertStatus {
        ACTIVE,
        IN_PROGRESS,
        RESOLVED,
        FALSE_ALARM,
        ESCALATED,
        CANCELLED,
        ARCHIVED
    }
    
    public enum PanicAction {
        NOTIFIED_EMERGENCY_CONTACTS,
        NOTIFIED_SECURITY_COMPANY,
        NOTIFIED_SAPS,
        INITIATED_LIVE_STREAM,
        DISPATCHED_SECURITY,
        CUSTOMER_CONTACTED,
        THERAPIST_CONTACTED,
        SITUATION_ASSESSED,
        INCIDENT_RESOLVED
    }
    
    @Embeddable
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationStatus {
        
        @Column(name = "emergency_contacts_notified")
        @Builder.Default
        private Boolean emergencyContacts = false;
        
        @Column(name = "security_company_notified")
        @Builder.Default
        private Boolean securityCompany = false;
        
        @Column(name = "saps_notified")
        @Builder.Default
        private Boolean saps = false;
        
        @Column(name = "saps_notified_at")
        private LocalDateTime sapsNotifiedAt;
        
        @Column(name = "saps_reference", length = 100)
        private String sapsReference;
        
        @Column(name = "emergency_contacts_count")
        @Builder.Default
        private Integer emergencyContactsCount = 0;
        
        @Column(name = "emergency_contacts_notified_at")
        private LocalDateTime emergencyContactsNotifiedAt;
        
        @Column(name = "security_company_notified_at")
        private LocalDateTime securityCompanyNotifiedAt;
        
        @Column(name = "admin_notified")
        @Builder.Default
        private Boolean adminNotified = false;
        
        @Column(name = "admin_notified_at")
        private LocalDateTime adminNotifiedAt;
        
        @Column(name = "customer_notified")
        @Builder.Default
        private Boolean customerNotified = false;
        
        @Column(name = "customer_notified_at")
        private LocalDateTime customerNotifiedAt;
        
        @Column(name = "therapist_notified")
        @Builder.Default
        private Boolean therapistNotified = false;
        
        @Column(name = "therapist_notified_at")
        private LocalDateTime therapistNotifiedAt;
    }
    
    @Embeddable
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SecurityCompany {
        
        @Column(name = "security_company_name", length = 200)
        private String name;
        
        @Column(name = "security_company_contact", length = 50)
        private String contact;
        
        @Column(name = "security_company_email", length = 100)
        private String email;
        
        @Column(name = "security_company_response_time", length = 50)
        private String responseTime; // e.g., "15 minutes"
        
        @Column(name = "security_company_address", length = 500)
        private String address;
        
        @Column(name = "security_company_phone", length = 20)
        private String phone;
        
        @Column(name = "security_company_license", length = 100)
        private String licenseNumber;
        
        @Column(name = "security_company_contact_person", length = 100)
        private String contactPerson;
    }
    
    @Embeddable
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ActionTaken {
        
        @Enumerated(EnumType.STRING)
        @Column(name = "action_type", length = 50)
        private PanicAction action;
        
        @Column(name = "action_timestamp", nullable = false)
        private LocalDateTime timestamp;
        
        @Column(name = "performed_by", length = 100)
        private String performedBy;
        
        @Column(name = "action_notes", columnDefinition = "TEXT")
        private String notes;
        
        @Column(name = "action_duration_minutes")
        private Integer durationMinutes;
        
        @Column(name = "action_outcome", length = 50)
        private String outcome; // success, failed, pending, partial
        
        @Column(name = "action_priority", length = 20)
        private String priority; // low, medium, high, critical
    }
    
    // Business logic methods
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (status == null) {
            status = AlertStatus.ACTIVE;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Calculate resolution time if resolved
        if (status == AlertStatus.RESOLVED && resolvedAt != null && timestamp != null) {
            long minutes = java.time.Duration.between(timestamp, resolvedAt).toMinutes();
            resolutionTimeMinutes = (int) minutes;
        }
        
        // Calculate response time
        if (acknowledgedAt != null && timestamp != null) {
            long minutes = java.time.Duration.between(timestamp, acknowledgedAt).toMinutes();
            responseTimeMinutes = (int) minutes;
        }
    }
    
    public void markAsResolved(String resolvedByUser, String notes) {
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolvedByUser;
        this.resolutionNotes = notes;
    }
    
    public void markAsFalseAlarm(String reason) {
        this.status = AlertStatus.FALSE_ALARM;
        this.isFalseAlarm = true;
        this.falseAlarmReason = reason;
        this.resolvedAt = LocalDateTime.now();
    }
    
    public void escalate(String escalatedToDepartment) {
        this.status = AlertStatus.ESCALATED;
        this.escalatedAt = LocalDateTime.now();
        this.escalatedTo = escalatedToDepartment;
    }
    
    public void acknowledge(String acknowledgedByUser) {
        this.acknowledgedAt = LocalDateTime.now();
        this.acknowledgedBy = acknowledgedByUser;
    }
    
    public void addAction(PanicAction action, String performedBy, String notes) {
        ActionTaken actionTaken = ActionTaken.builder()
            .action(action)
            .timestamp(LocalDateTime.now())
            .performedBy(performedBy)
            .notes(notes)
            .build();
        
        if (actionsTaken == null) {
            actionsTaken = new ArrayList<>();
        }
        actionsTaken.add(actionTaken);
    }
    
    public boolean isActive() {
        return status == AlertStatus.ACTIVE || status == AlertStatus.IN_PROGRESS;
    }
    
    public boolean isResolved() {
        return status == AlertStatus.RESOLVED || status == AlertStatus.FALSE_ALARM;
    }
    
    public boolean isHighPriority() {
        return priority != null && priority >= 4; // 4 or 5 is high priority
    }
    
    // Indexes for better query performance
    @Table(name = "security_alerts", indexes = {
        @Index(name = "idx_security_alert_type", columnList = "alert_type"),
        @Index(name = "idx_security_alert_status", columnList = "status"),
        @Index(name = "idx_security_alert_timestamp", columnList = "timestamp"),
        @Index(name = "idx_security_alert_user_id", columnList = "user_id"),
        @Index(name = "idx_security_alert_booking_id", columnList = "booking_id"),
        @Index(name = "idx_security_alert_priority", columnList = "priority"),
        @Index(name = "idx_security_alert_is_flashing", columnList = "is_flashing"),
        @Index(name = "idx_security_alert_city", columnList = "city"),
        @Index(name = "idx_security_alert_suburb", columnList = "suburb"),
        @Index(name = "idx_security_alert_created_at", columnList = "created_at"),
        @Index(name = "idx_security_alert_status_timestamp", columnList = "status, timestamp"),
        @Index(name = "idx_security_alert_priority_status", columnList = "priority, status")
    })
    static class SecurityAlertTableIndices {}
}