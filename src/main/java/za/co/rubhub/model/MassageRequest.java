package za.co.rubhub.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "massage_requests")
public class MassageRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "client_name", length = 100, nullable = false)
    @NotBlank(message = "Client name is required")
    private String clientName;
    
    @Column(name = "client_phone", length = 20, nullable = false)
    @NotBlank(message = "Client phone is required")
    @Pattern(regexp = "^[0-9\\+\\-\\s()]{10,20}$", message = "Invalid phone number format")
    private String clientPhone;
    
    @Column(name = "client_email", length = 100)
    @Email(message = "Invalid email format")
    private String clientEmail;
    
    @Column(name = "service_type", length = 50, nullable = false)
    @NotBlank(message = "Service type is required")
    private String serviceType;
    
    @Column(name = "duration_minutes", nullable = false)
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Duration must be at least 15 minutes")
    @Max(value = 240, message = "Duration cannot exceed 240 minutes")
    private Integer durationMinutes;
    
    @Column(name = "preferred_date_time", nullable = false)
    @NotNull(message = "Preferred date and time is required")
    @Future(message = "Preferred date must be in the future")
    private LocalDateTime preferredDateTime;
    
    @Column(name = "location_address", length = 500, nullable = false)
    @NotBlank(message = "Location address is required")
    private String locationAddress;
    
    @Column(name = "location_latitude")
    private Double locationLatitude;
    
    @Column(name = "location_longitude")
    private Double locationLongitude;
    
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", length = 20)
    private UrgencyLevel urgencyLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull(message = "Status is required")
    private Status status = Status.PENDING;
    
    @Column(name = "estimated_price", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal estimatedPrice;
    
    @Column(name = "actual_price", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal actualPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus;
    
    @Column(name = "client_rating_score")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer clientRatingScore;
    
    @Column(name = "client_rating_review", columnDefinition = "TEXT")
    private String clientRatingReview;
    
    @Column(name = "client_rating_date")
    private LocalDateTime clientRatingDate;
    
    @Column(name = "cancellation_reason", length = 200)
    private String cancellationReason;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id")
    private Therapist therapist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;
    
    // Remove the inner Rating class and use enums instead
    
    // Enums for better type safety
    public enum UrgencyLevel {
        LOW, MEDIUM, HIGH, EMERGENCY
    }
    
    public enum Status {
        PENDING, ASSIGNED, ACCEPTED, REJECTED, CANCELLED, COMPLETED
    }
    
    public enum PaymentStatus {
        PENDING, PAID, REFUNDED, PARTIALLY_REFUNDED, FAILED
    }
    
    // Timestamp handlers
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public MassageRequest() {}
    
    public MassageRequest(String clientName, String clientPhone, String serviceType, 
                         Integer durationMinutes, LocalDateTime preferredDateTime, 
                         String locationAddress) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.serviceType = serviceType;
        this.durationMinutes = durationMinutes;
        this.preferredDateTime = preferredDateTime;
        this.locationAddress = locationAddress;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business logic methods
    public void assignToTherapist(Therapist therapist) {
        this.therapist = therapist;
        this.status = Status.ASSIGNED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void acceptRequest() {
        if (this.status == Status.ASSIGNED) {
            this.status = Status.ACCEPTED;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void rejectRequest(String reason) {
        this.status = Status.REJECTED;
        this.cancellationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancelRequest(String reason) {
        this.status = Status.CANCELLED;
        this.cancellationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void completeRequest(BigDecimal actualPrice, Integer ratingScore, String feedback) {
        this.status = Status.COMPLETED;
        this.actualPrice = actualPrice;
        if (ratingScore != null) {
            this.clientRatingScore = ratingScore;
            this.clientRatingReview = feedback;
            this.clientRatingDate = LocalDateTime.now();
        }
        this.paymentStatus = PaymentStatus.PAID;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addRating(Integer score, String review) {
        if (score != null && score >= 1 && score <= 5) {
            this.clientRatingScore = score;
            this.clientRatingReview = review;
            this.clientRatingDate = LocalDateTime.now();
        }
    }
    
    public boolean isPending() {
        return this.status == Status.PENDING;
    }
    
    public boolean isAssigned() {
        return this.status == Status.ASSIGNED;
    }
    
    public boolean isAccepted() {
        return this.status == Status.ACCEPTED;
    }
    
    public boolean isCompleted() {
        return this.status == Status.COMPLETED;
    }
    
    public boolean isCancelledOrRejected() {
        return this.status == Status.CANCELLED || this.status == Status.REJECTED;
    }
    
    public boolean canBeCancelled() {
        return this.status == Status.PENDING || 
               this.status == Status.ASSIGNED || 
               this.status == Status.ACCEPTED;
    }
    
    public boolean canBeRated() {
        return this.status == Status.COMPLETED && 
               this.clientRatingScore == null && 
               this.clientRatingDate == null;
    }
    
public String getRatingStars() {
    if (clientRatingScore == null) return "Not rated";
    
    StringBuilder stars = new StringBuilder();
    // Add filled stars
    for (int i = 0; i < clientRatingScore; i++) {
        stars.append("★");
    }
    // Add empty stars
    for (int i = clientRatingScore; i < 5; i++) {
        stars.append("☆");
    }
    return stars.toString();
}
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    
    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }
    
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public LocalDateTime getPreferredDateTime() { return preferredDateTime; }
    public void setPreferredDateTime(LocalDateTime preferredDateTime) { this.preferredDateTime = preferredDateTime; }
    
    public String getLocationAddress() { return locationAddress; }
    public void setLocationAddress(String locationAddress) { this.locationAddress = locationAddress; }
    
    public Double getLocationLatitude() { return locationLatitude; }
    public void setLocationLatitude(Double locationLatitude) { this.locationLatitude = locationLatitude; }
    
    public Double getLocationLongitude() { return locationLongitude; }
    public void setLocationLongitude(Double locationLongitude) { this.locationLongitude = locationLongitude; }
    
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    
    public UrgencyLevel getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(UrgencyLevel urgencyLevel) { this.urgencyLevel = urgencyLevel; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status pending) { this.status = pending; }
    
    public BigDecimal getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(BigDecimal estimatedPrice) { this.estimatedPrice = estimatedPrice; }
    
    public BigDecimal getActualPrice() { return actualPrice; }
    public void setActualPrice(BigDecimal actualPrice) { this.actualPrice = actualPrice; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public Integer getClientRatingScore() { return clientRatingScore; }
    public void setClientRatingScore(Integer clientRatingScore) { 
        if (clientRatingScore != null && (clientRatingScore < 1 || clientRatingScore > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.clientRatingScore = clientRatingScore; 
    }
    
    public String getClientRatingReview() { return clientRatingReview; }
    public void setClientRatingReview(String clientRatingReview) { this.clientRatingReview = clientRatingReview; }
    
    public LocalDateTime getClientRatingDate() { return clientRatingDate; }
    public void setClientRatingDate(LocalDateTime clientRatingDate) { this.clientRatingDate = clientRatingDate; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Therapist getTherapist() { return therapist; }
    public void setTherapist(Therapist therapist) { this.therapist = therapist; }
    
    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }

    // Remove or fix these methods - they seem to be placeholders
    public String getCustomerId() {
        return client != null ? client.getId().toString() : null;
    }

    public void setCustomer(User user) {
        this.client = user;
    }

    public String getSpecialRequests() {
        return specialInstructions;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialInstructions = specialRequests;
    }

    public void calculateTotals() {
        // Calculate total based on duration and service type
        // This is a placeholder - implement your actual pricing logic
        if (estimatedPrice == null && durationMinutes != null) {
            // Example: R500 per hour
            BigDecimal ratePerMinute = new BigDecimal("8.33"); // R500 / 60
            this.estimatedPrice = ratePerMinute.multiply(new BigDecimal(durationMinutes));
        }
    }

    public void setClientRatingScore(String ratingStars) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setClientRatingScore'");
    }

    public Object getClientFeedback() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getClientFeedback'");
    }

    public void setClientFeedback(Object clientFeedback) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setClientFeedback'");
    }

    public void setStatus(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setStatus'");
    }
}