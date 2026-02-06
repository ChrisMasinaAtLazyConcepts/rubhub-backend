package za.co.rubhub.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import za.co.rubhub.model.Booking;
import za.co.rubhub.model.Payment;
import za.co.rubhub.model.TherapistPayout;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;
    
    @Column(name = "user_id", nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @Column(name = "reference", nullable = false, length = 100)
    @NotBlank(message = "Reference is required")
    private String reference;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_category", nullable = false, length = 50)
    @NotNull(message = "Transaction category is required")
    private TransactionCategory transactionCategory;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @Column(name = "currency", nullable = false, length = 3)
    @NotBlank(message = "Currency is required")
    @Builder.Default
    private String currency = "ZAR";
    
    @Column(name = "fee", precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Fee cannot be negative")
    @Builder.Default
    private BigDecimal fee = BigDecimal.ZERO;
    
    @Column(name = "tax", precision = 15, scale = 2)
    @DecimalMin(value = "0.00", message = "Tax cannot be negative")
    @Builder.Default
    private BigDecimal tax = BigDecimal.ZERO;
    
    @Column(name = "net_amount", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Net amount is required")
    @DecimalMin(value = "0.00", message = "Net amount cannot be negative")
    private BigDecimal netAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Transaction status is required")
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;
    
    @Column(name = "status_reason", length = 500)
    private String statusReason;
    
    // Account Information
    @Column(name = "from_account_id", length = 100)
    private String fromAccountId;
    
    @Column(name = "from_account_name", length = 200)
    private String fromAccountName;
    
    @Column(name = "from_account_type", length = 50)
    private String fromAccountType;
    
    @Column(name = "to_account_id", length = 100)
    private String toAccountId;
    
    @Column(name = "to_account_name", length = 200)
    private String toAccountName;
    
    @Column(name = "to_account_type", length = 50)
    private String toAccountType;
    
    // Related Entities
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payout_id")
    private TherapistPayout payout;
    
    @Column(name = "booking_id_string")
    private String bookingIdString;
    
    @Column(name = "payment_id_string")
    private String paymentIdString;
    
    @Column(name = "payout_id_string")
    private String payoutIdString;
    
    // Payment Method
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;
    
    @Column(name = "gateway_transaction_id", length = 100)
    private String gatewayTransactionId;
    
    @Column(name = "gateway_reference", length = 100)
    private String gatewayReference;
    
    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;
    
    // Timing Information
    @Column(name = "transaction_date", nullable = false)
    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactionDate;
    
    @Column(name = "processed_date")
    private LocalDateTime processedDate;
    
    @Column(name = "completed_date")
    private LocalDateTime completedDate;
    
    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;
    
    @Column(name = "failed_date")
    private LocalDateTime failedDate;
    
    @Column(name = "reconciled_date")
    private LocalDateTime reconciledDate;
    
    // Location Information
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    @Column(name = "device_id", length = 100)
    private String deviceId;
    
    @Column(name = "location", length = 200)
    private String location;
    
    // Security Information
    @Column(name = "security_token", length = 100)
    private String securityToken;
    
    @Column(name = "is_secure", nullable = false)
    @Builder.Default
    private Boolean isSecure = true;
    
    @Column(name = "fraud_score")
    private Integer fraudScore;
    
    @Column(name = "fraud_check_performed")
    @Builder.Default
    private Boolean fraudCheckPerformed = false;
    
    @Column(name = "fraud_check_result", length = 50)
    private String fraudCheckResult;
    
    // Audit Information
    @Column(name = "initiated_by", length = 100)
    private String initiatedBy;
    
    @Column(name = "approved_by", length = 100)
    private String approvedBy;
    
    @Column(name = "rejected_by", length = 100)
    private String rejectedBy;
    
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;
    
    // Metadata
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "tags", columnDefinition = "jsonb")
    private String tags;
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum TransactionType {
        // Payment Types
        PAYMENT,
        REFUND,
        PARTIAL_REFUND,
        CHARGEBACK,
        
        // Payout Types
        PAYOUT,
        COMMISSION,
        BONUS,
        
        // Transfer Types
        TRANSFER,
        DEPOSIT,
        WITHDRAWAL,
        
        // Adjustment Types
        ADJUSTMENT,
        CORRECTION,
        REVERSAL,
        
        // Fee Types
        SERVICE_FEE,
        PROCESSING_FEE,
        LATE_FEE,
        CANCELLATION_FEE,
        
        // Loyalty Types
        LOYALTY_POINTS_EARNED,
        LOYALTY_POINTS_REDEEMED,
        CREDIT_EARNED,
        CREDIT_REDEEMED,
        
        // Other Types
        SUBSCRIPTION,
        RENEWAL,
        DONATION
    }
    
    public enum TransactionCategory {
        INCOME,
        EXPENSE,
        TRANSFER,
        FEE,
        REFUND,
        ADJUSTMENT,
        LOYALTY,
        OTHER
    }
    
    public enum TransactionStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED,
        REVERSED,
        REFUNDED,
        CHARGEBACK,
        DISPUTED,
        ON_HOLD,
        SCHEDULED
    }
    
    // Constructors
    public Transaction(String transactionId, String userId, String reference, 
                      TransactionType transactionType, TransactionCategory transactionCategory,
                      String description, BigDecimal amount, String currency) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.reference = reference;
        this.transactionType = transactionType;
        this.transactionCategory = transactionCategory;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.fee = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.netAmount = amount;
        this.status = TransactionStatus.PENDING;
        this.transactionDate = LocalDateTime.now();
        this.isSecure = true;
    }
    
    // Business logic methods
    @PrePersist
    protected void onCreate() {
        if (transactionId == null) {
            transactionId = "TXN-" + System.currentTimeMillis() + "-" + 
                           UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (reference == null) {
            reference = transactionId;
        }
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
        if (currency == null) {
            currency = "ZAR";
        }
        calculateNetAmount();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calculateNetAmount();
        
        // Update status timestamps
        switch (status) {
            case PROCESSING:
                if (processedDate == null) {
                    processedDate = LocalDateTime.now();
                }
                break;
            case COMPLETED:
                if (completedDate == null) {
                    completedDate = LocalDateTime.now();
                }
                break;
            case CANCELLED:
                if (cancelledDate == null) {
                    cancelledDate = LocalDateTime.now();
                }
                break;
            case FAILED:
                if (failedDate == null) {
                    failedDate = LocalDateTime.now();
                }
                break;
        }
    }
    
    private void calculateNetAmount() {
        if (amount != null) {
            BigDecimal totalDeductions = BigDecimal.ZERO;
            if (fee != null) {
                totalDeductions = totalDeductions.add(fee);
            }
            if (tax != null) {
                totalDeductions = totalDeductions.add(tax);
            }
            this.netAmount = amount.subtract(totalDeductions);
            
            // Ensure net amount is not negative
            if (this.netAmount.compareTo(BigDecimal.ZERO) < 0) {
                this.netAmount = BigDecimal.ZERO;
            }
        }
    }
    
    public void markAsProcessing() {
        this.status = TransactionStatus.PROCESSING;
        this.processedDate = LocalDateTime.now();
    }
    
    public void markAsCompleted() {
        this.status = TransactionStatus.COMPLETED;
        this.completedDate = LocalDateTime.now();
    }
    
    public void markAsFailed(String reason) {
        this.status = TransactionStatus.FAILED;
        this.statusReason = reason;
        this.failedDate = LocalDateTime.now();
    }
    
    public void markAsCancelled(String reason) {
        this.status = TransactionStatus.CANCELLED;
        this.statusReason = reason;
        this.cancelledDate = LocalDateTime.now();
    }
    
    public void markAsRefunded() {
        this.status = TransactionStatus.REFUNDED;
    }
    
    public void addFee(BigDecimal feeAmount, String feeDescription) {
        if (feeAmount != null && feeAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.fee = this.fee.add(feeAmount);
            if (this.description == null) {
                this.description = feeDescription;
            } else {
                this.description += "; " + feeDescription;
            }
            calculateNetAmount();
        }
    }
    
    public void addTax(BigDecimal taxAmount) {
        if (taxAmount != null && taxAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.tax = this.tax.add(taxAmount);
            calculateNetAmount();
        }
    }
    
    public boolean isIncome() {
        return transactionCategory == TransactionCategory.INCOME;
    }
    
    public boolean isExpense() {
        return transactionCategory == TransactionCategory.EXPENSE;
    }
    
    public boolean isTransfer() {
        return transactionCategory == TransactionCategory.TRANSFER;
    }
    
    public boolean isCompleted() {
        return status == TransactionStatus.COMPLETED;
    }
    
    public boolean isPending() {
        return status == TransactionStatus.PENDING || status == TransactionStatus.PROCESSING;
    }
    
    public boolean isFailedOrCancelled() {
        return status == TransactionStatus.FAILED || 
               status == TransactionStatus.CANCELLED || 
               status == TransactionStatus.REVERSED;
    }
    
    public boolean canBeRefunded() {
        return status == TransactionStatus.COMPLETED && 
               (transactionType == TransactionType.PAYMENT || 
                transactionType == TransactionType.SUBSCRIPTION);
    }
    
    public boolean canBeCancelled() {
        return status == TransactionStatus.PENDING || 
               status == TransactionStatus.PROCESSING;
    }
    
    public String getFormattedAmount() {
        return String.format("%s %.2f", currency, amount != null ? amount : BigDecimal.ZERO);
    }
    
    public String getFormattedNetAmount() {
        return String.format("%s %.2f", currency, netAmount != null ? netAmount : BigDecimal.ZERO);
    }
    
    public String getFormattedFee() {
        return String.format("%s %.2f", currency, fee != null ? fee : BigDecimal.ZERO);
    }
    
    public String getStatusIcon() {
        switch (status) {
            case COMPLETED: return "✅";
            case PROCESSING: return "🔄";
            case PENDING: return "⏳";
            case FAILED: return "❌";
            case CANCELLED: return "🚫";
            case REFUNDED: return "↩️";
            default: return "📄";
        }
    }
    
    public String getFormattedStatus() {
        return getStatusIcon() + " " + status.toString();
    }
    
    // Validation method
    @AssertTrue(message = "Transaction amount must match net amount plus fees and taxes")
    public boolean isAmountConsistent() {
        if (amount == null || netAmount == null) return true;
        
        BigDecimal calculatedAmount = netAmount;
        if (fee != null) {
            calculatedAmount = calculatedAmount.add(fee);
        }
        if (tax != null) {
            calculatedAmount = calculatedAmount.add(tax);
        }
        
        return amount.compareTo(calculatedAmount) == 0;
    }
    
    // Indexes for better query performance
    @Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_transaction_id", columnList = "transaction_id", unique = true),
        @Index(name = "idx_transaction_user_id", columnList = "user_id"),
        @Index(name = "idx_transaction_reference", columnList = "reference"),
        @Index(name = "idx_transaction_type", columnList = "transaction_type"),
        @Index(name = "idx_transaction_category", columnList = "transaction_category"),
        @Index(name = "idx_transaction_status", columnList = "status"),
        @Index(name = "idx_transaction_date", columnList = "transaction_date"),
        @Index(name = "idx_transaction_from_account", columnList = "from_account_id"),
        @Index(name = "idx_transaction_to_account", columnList = "to_account_id"),
        @Index(name = "idx_transaction_booking", columnList = "booking_id"),
        @Index(name = "idx_transaction_payment", columnList = "payment_id"),
        @Index(name = "idx_transaction_payout", columnList = "payout_id"),
        @Index(name = "idx_transaction_gateway_id", columnList = "gateway_transaction_id"),
        @Index(name = "idx_transaction_created_at", columnList = "created_at"),
        @Index(name = "idx_transaction_user_status", columnList = "user_id, status"),
        @Index(name = "idx_transaction_date_status", columnList = "transaction_date, status"),
        @Index(name = "idx_transaction_type_status", columnList = "transaction_type, status")
    })
    static class TransactionTableIndices {}
}