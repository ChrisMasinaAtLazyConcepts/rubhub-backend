package za.co.rubhub.model;

import javax.persistence.*;
import za.co.rubhub.model.*;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import za.co.rubhub.model.*;

@Entity
@Table(name = "payments")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true)
    @NotBlank(message = "Transaction ID is required")
    private String transactionId;

    @Column(name = "user_id", nullable = false)
    @NotBlank(message = "User ID is required")
    private String userId;

    @Column(name = "booking_id", nullable = false)
    @NotBlank(message = "Booking ID is required")
    private String bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", referencedColumnName = "id")
    private Therapist therapist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Booking booking;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Column(name = "therapist_earnings", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Therapist earnings are required")
    @DecimalMin(value = "0.00", message = "Therapist earnings cannot be negative")
    private BigDecimal therapistEarnings;

    @Column(name = "platform_fee", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Platform fee is required")
    @DecimalMin(value = "0.00", message = "Platform fee cannot be negative")
    private BigDecimal platformFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    @NotNull(message = "Currency is required")
    private Currency currency = Currency.ZAR;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    @NotNull(message = "Payment status is required")
    private PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @Column(name = "provider", length = 50)
    private String provider; // e.g., "Visa", "PayFast", etc.

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    @Column(name = "payout_date")
    private LocalDateTime payoutDate;
    
    @Column(name = "provider_transaction_id", length = 100)
    private String providerTransactionId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Embedded
    private BillingInfo billingInfo;

    @Embedded
    private CardDetails cardDetails;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum Currency {
        ZAR, USD, EUR
    }

    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, 
        PARTIALLY_REFUNDED, CANCELLED
    }

    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYFAST, BANK_TRANSFER, PAYPAL
    }

    // Embeddable classes
    @Embeddable
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingInfo {
        
        @Column(name = "billing_first_name", nullable = false, length = 50)
        @NotBlank(message = "First name is required")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        private String firstName;

        @Column(name = "billing_last_name", nullable = false, length = 50)
        @NotBlank(message = "Last name is required")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        private String lastName;

        @Column(name = "billing_email", nullable = false, length = 100)
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @Column(name = "billing_phone", nullable = false, length = 20)
        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
        private String phone;

        @Column(name = "billing_address_line1", nullable = false, length = 200)
        @NotBlank(message = "Address line 1 is required")
        private String addressLine1;

        @Column(name = "billing_address_line2", length = 200)
        private String addressLine2;

        @Column(name = "billing_city", nullable = false, length = 100)
        @NotBlank(message = "City is required")
        private String city;

        @Column(name = "billing_state", length = 100)
        private String state;

        @Column(name = "billing_postal_code", nullable = false, length = 20)
        @NotBlank(message = "Postal code is required")
        private String postalCode;

        @Column(name = "billing_country", nullable = false, length = 2)
        @NotBlank(message = "Country is required")
        private String country;

        public BillingInfo(String firstName, String lastName, String email, String phone, 
                          String addressLine1, String city, String postalCode, String country) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.phone = phone;
            this.addressLine1 = addressLine1;
            this.city = city;
            this.postalCode = postalCode;
            this.country = country;
        }
    }

   

    @Embeddable
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardDetails {
        
        @Column(name = "card_last4", nullable = false, length = 4)
        @NotBlank(message = "Last 4 digits are required")
        @Pattern(regexp = "^[0-9]{4}$", message = "Last 4 digits must be exactly 4 numbers")
        private String last4;

        @Column(name = "card_brand", nullable = false, length = 20)
        @NotBlank(message = "Card brand is required")
        private String brand; // visa, mastercard, etc.

        @Column(name = "card_expiry_month", nullable = false)
        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 1 and 12")
        @Max(value = 12, message = "Expiry month must be between 1 and 12")
        private Integer expiryMonth;

        @Column(name = "card_expiry_year", nullable = false)
        @NotNull(message = "Expiry year is required")
        @Min(value = 2024, message = "Expiry year must be current or future")
        private Integer expiryYear;

        @Column(name = "card_funding", length = 20)
        private String funding; // credit, debit

        @Column(name = "card_country", length = 2)
        private String country;

        @Column(name = "card_token", length = 100)
        private String token;

        @Column(name = "card_fingerprint", length = 100)
        private String fingerprint;

        public CardDetails(String last4, String brand, Integer expiryMonth, Integer expiryYear) {
            this.last4 = last4;
            this.brand = brand;
            this.expiryMonth = expiryMonth;
            this.expiryYear = expiryYear;
        }
    }

    // Constructors
    public Payment(String transactionId, String userId, String bookingId, 
                  Therapist therapist, Booking booking, BigDecimal amount, 
                  BigDecimal therapistEarnings, BigDecimal platformFee, PaymentMethod paymentMethod) {
        this();
        this.transactionId = transactionId;
        this.userId = userId;
        this.bookingId = bookingId;
        this.therapist = therapist;
        this.booking = booking;
        this.amount = amount;
        this.therapistEarnings = therapistEarnings;
        this.platformFee = platformFee;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.paymentDate = LocalDateTime.now();
    }

    // Convenience constructor with Double amounts
    public Payment(String transactionId, String userId, String bookingId, 
                  Therapist therapist, Booking booking, Double amount, 
                  Double therapistEarnings, Double platformFee, PaymentMethod paymentMethod) {
        this(
            transactionId, userId, bookingId, therapist, booking,
            BigDecimal.valueOf(amount),
            BigDecimal.valueOf(therapistEarnings),
            BigDecimal.valueOf(platformFee),
            paymentMethod
        );
    }

    // Business logic methods
    @PrePersist
    protected void onCreate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        
        // Calculate platform fee if not set (default 15%)
        if (platformFee == null && amount != null) {
            platformFee = amount.multiply(new BigDecimal("0.15"));
        }
        
        // Calculate therapist earnings if not set
        if (therapistEarnings == null && amount != null && platformFee != null) {
            therapistEarnings = amount.subtract(platformFee);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void markAsCompleted(String providerTransactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.processedDate = LocalDateTime.now();
        this.providerTransactionId = providerTransactionId;
    }

    public void markAsProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    public void markAsFailed(String errorMessage) {
        this.status = PaymentStatus.FAILED;
        if (notes == null) {
            notes = errorMessage;
        } else {
            notes += "\n" + errorMessage;
        }
    }

    public void processPayout() {
        if (canProcessPayout()) {
            this.payoutDate = LocalDateTime.now();
        }
    }

    public boolean isSuccess() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public boolean canProcessPayout() {
        return this.status == PaymentStatus.COMPLETED && this.payoutDate == null;
    }

    public boolean isRefunded() {
        return this.status == PaymentStatus.REFUNDED || 
               this.status == PaymentStatus.PARTIALLY_REFUNDED;
    }

    // Helper methods for amount calculations
    public BigDecimal calculatePlatformFee(BigDecimal commissionRate) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(commissionRate);
    }

    public BigDecimal calculateTherapistEarnings(BigDecimal commissionRate) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal fee = calculatePlatformFee(commissionRate);
        return amount.subtract(fee);
    }

    // JSON representation for API responses
    @Transient
    public String getFormattedAmount() {
        return amount != null ? String.format("%.2f %s", amount, currency) : "0.00 " + currency;
    }

    @Transient
    public String getFormattedTherapistEarnings() {
        return therapistEarnings != null ? String.format("%.2f %s", therapistEarnings, currency) : "0.00 " + currency;
    }

    // Validation helper
    @AssertTrue(message = "Therapist earnings plus platform fee must equal total amount")
    public boolean isAmountDistributionValid() {
        if (amount == null || therapistEarnings == null || platformFee == null) {
            return true; // Let other validations handle nulls
        }
        
        BigDecimal total = therapistEarnings.add(platformFee);
        return amount.compareTo(total) == 0;
    }


    // Indexes for better query performance
    @Table(name = "payments", indexes = {
        @Index(name = "idx_payment_transaction_id", columnList = "transaction_id", unique = true),
        @Index(name = "idx_payment_user_id", columnList = "user_id"),
        @Index(name = "idx_payment_booking_id", columnList = "booking_id"),
        @Index(name = "idx_payment_therapist_id", columnList = "therapist_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_method", columnList = "payment_method"),
        @Index(name = "idx_payment_date", columnList = "payment_date"),
        @Index(name = "idx_payment_created_at", columnList = "created_at"),
        @Index(name = "idx_payment_provider_transaction_id", columnList = "provider_transaction_id")
    })
    static class PaymentTableIndices {}
}