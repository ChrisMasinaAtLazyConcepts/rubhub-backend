package za.co.rubhub.model;

import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "therapist_payouts")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TherapistPayout {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    @Column(name = "therapist_id_string")
    private String therapistIdString;
    
    @Column(name = "booking_id_string")
    private String bookingIdString;
    
    @Column(name = "booking_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal bookingAmount;
    
    @Column(name = "rubhub_fee", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal rubhubFee = BigDecimal.ZERO; // 12%
    
    @Column(name = "therapist_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal therapistAmount;
    
    @Column(name = "rubhub_earnings", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal rubhubEarnings = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", nullable = false, length = 20)
    @Builder.Default
    private PayoutStatus payoutStatus = PayoutStatus.PENDING;
    
    @Column(name = "transaction_id", length = 100)
    private String transactionId;
    
    @Column(name = "payout_date")
    private LocalDateTime payoutDate;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
    
    // Additional fields
    @Column(name = "payfast_payout_id", length = 100)
    private String payfastPayoutId;
    
    @Column(name = "reference", length = 100)
    private String reference;
    
    @Column(name = "fee", precision = 10, scale = 2)
    private BigDecimal fee; // PayFast fee
    
    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount;
    
    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "ZAR";
    
    @Column(name = "attempt_count")
    @Builder.Default
    private Integer attemptCount = 0;
    
    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum PayoutStatus {
        PENDING, PROCESSING, PROCESSED, FAILED, CANCELLED, REVERSED
    }
    
    // Constructors
    public TherapistPayout(Booking booking) {
        this.booking = booking;
        this.therapist = booking.getTherapist();
        this.therapistIdString = booking.getTherapist() != null ? 
            booking.getTherapist().getId().toString() : null;
        this.bookingIdString = booking.getId() != null ? booking.getId().toString() : null;
        this.bookingAmount = booking.getTotalAmount();
        this.rubhubFee = booking.getRubhubServiceFee();
        this.therapistAmount = booking.getTherapistEarnings();
        this.rubhubEarnings = this.rubhubFee;
        this.payoutStatus = PayoutStatus.PENDING;
        this.payoutDate = LocalDateTime.now();
    }
    
    // Business logic methods
    @PrePersist
    protected void onCreate() {
        if (payoutDate == null) {
            payoutDate = LocalDateTime.now();
        }
        if (reference == null) {
            reference = "PAYOUT-" + System.currentTimeMillis();
        }
        
        // Calculate amounts if not set
        if (rubhubFee == null && bookingAmount != null) {
            rubhubFee = bookingAmount.multiply(new BigDecimal("0.12"));
        }
        
        if (therapistAmount == null && bookingAmount != null && rubhubFee != null) {
            therapistAmount = bookingAmount.subtract(rubhubFee);
        }
        
        if (rubhubEarnings == null) {
            rubhubEarnings = rubhubFee != null ? rubhubFee : BigDecimal.ZERO;
        }
        
        if (netAmount == null && therapistAmount != null && fee != null) {
            netAmount = therapistAmount.subtract(fee);
        }
    }
    
    public void markAsProcessing(String transactionId) {
        this.payoutStatus = PayoutStatus.PROCESSING;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
        this.attemptCount++;
        this.lastAttemptAt = LocalDateTime.now();
    }
    
    public void markAsProcessed(String payfastPayoutId) {
        this.payoutStatus = PayoutStatus.PROCESSED;
        this.payfastPayoutId = payfastPayoutId;
        this.processedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.payoutStatus = PayoutStatus.FAILED;
        this.failureReason = reason;
        this.attemptCount++;
        this.lastAttemptAt = LocalDateTime.now();
    }
    
    public boolean canRetry() {
        return this.payoutStatus == PayoutStatus.FAILED && 
               this.attemptCount < 3; // Max 3 attempts
    }
    
    public String getFormattedBookingAmount() {
        return String.format("R %.2f", bookingAmount != null ? bookingAmount : BigDecimal.ZERO);
    }
    
    public String getFormattedTherapistAmount() {
        return String.format("R %.2f", therapistAmount != null ? therapistAmount : BigDecimal.ZERO);
    }
    
    // Indexes for better query performance
    @Table(name = "therapist_payouts", indexes = {
        @Index(name = "idx_payout_therapist_id", columnList = "therapist_id"),
        @Index(name = "idx_payout_booking_id", columnList = "booking_id"),
        @Index(name = "idx_payout_status", columnList = "payout_status"),
        @Index(name = "idx_payout_transaction_id", columnList = "transaction_id"),
        @Index(name = "idx_payout_payfast_id", columnList = "payfast_payout_id"),
        @Index(name = "idx_payout_reference", columnList = "reference"),
        @Index(name = "idx_payout_created_at", columnList = "created_at"),
        @Index(name = "idx_payout_payout_date", columnList = "payout_date"),
        @Index(name = "idx_payout_therapist_status", columnList = "therapist_id, payout_status")
    })
    static class TherapistPayoutTableIndices {}
}