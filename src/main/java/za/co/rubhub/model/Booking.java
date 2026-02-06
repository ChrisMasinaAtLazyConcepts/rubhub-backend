package za.co.rubhub.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "booking_reference", unique = true, length = 50)
    private String bookingReference;
    
    @Column(name = "customer_name", length = 100, nullable = false)
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @Column(name = "customer_email", length = 100, nullable = false)
    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    @Column(name = "customer_phone", length = 20)
    private String customerPhone;
    
    // ONLY ONE address field - choose ONE option:
    
    // Option 1: Simple address field
    @Column(name = "address", length = 500)
    private String address;
    
    // Option 2: If you need more details, use these separate fields (COMMENT OUT the above)
    /*
    @Column(name = "street_address", length = 255)
    private String streetAddress;
    
    @Column(name = "city", length = 100)
    private String city;
    
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Column(name = "country", length = 100)
    private String country;
    */
    
    @ManyToOne
    @NotBlank(message = "Service type is required")
    private MassageServiceType serviceType;
    
    @Column(name = "duration_minutes", nullable = false)
    @NotNull(message = "Duration is required")
    @Min(value = 15, message = "Minimum duration is 15 minutes")
    @Max(value = 240, message = "Maximum duration is 240 minutes")
    private Integer durationMinutes;
    
    @Column(name = "booking_date", nullable = false)
    @NotNull(message = "Booking date is required")
    private LocalDateTime bookingDate;
    
    @Column(name = "preferred_time", length = 20)
    private String preferredTime;
    
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;
    
    @Column(name = "status", length = 20, nullable = false)
    @NotBlank(message = "Status is required")
    private BookingStatus status = BookingStatus.PENDING; // PENDING, CONFIRMED, CANCELLED, COMPLETED
    
    @Column(name = "price", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal price;
    
    @Column(name = "discount", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    private BigDecimal discount;
    
    @Column(name = "total_amount", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Total amount cannot be negative")
    private BigDecimal totalAmount;
    
    @Column(name = "payment_status", length = 20)
    private String paymentStatus = "PENDING"; // PENDING, PAID, REFUNDED
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
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
    @JoinColumn(name = "user_id")
    private User customer;
    
       public enum Rating {
        ONE_STAR(1, "Poor"),
        TWO_STARS(2, "Fair"),
        THREE_STARS(3, "Good"),
        FOUR_STARS(4, "Very Good"),
        FIVE_STARS(5, "Excellent");
        
        private final int score;
        private final String description;
        
        Rating(int score, String description) {
            this.score = score;
            this.description = description;
        }
        
        public int getScore() { return score; }
        public String getDescription() { return description; }
    }
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rating")
    private Rating rating;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // Generate booking reference if not set
        if (this.bookingReference == null || this.bookingReference.isEmpty()) {
            this.bookingReference = "BK" + System.currentTimeMillis() + 
                                   (int)(Math.random() * 1000);
        }
        
        // Calculate total if not set
        calculateTotal();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        calculateTotal();
    }
    
    private void calculateTotal() {
        if (this.price != null) {
            BigDecimal discountAmount = this.discount != null ? this.discount : BigDecimal.ZERO;
            this.totalAmount = this.price.subtract(discountAmount);
            
            // Ensure total is not negative
            if (this.totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                this.totalAmount = BigDecimal.ZERO;
            }
        }
    }
    
    // Business methods
    public void confirmBooking() {
        this.status = BookingStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancelBooking(String reason) {
        this.status = BookingStatus.CANCELLED;
        this.cancellationReason = reason;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void completeBooking() {
        this.status = BookingStatus.COMPLETED;
        this.paymentStatus = "PAID";
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }
    
    public boolean isConfirmed() {
        return "CONFIRMED".equals(this.status);
    }
    
    public boolean isCancelled() {
        return "CANCELLED".equals(this.status);
    }
    
    public boolean isCompleted() {
        return "COMPLETED".equals(this.status);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Rating getRating() { return rating; }

    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public MassageServiceType getServiceType() { return serviceType; }
    public void setServiceType(MassageServiceType serviceType) { this.serviceType = serviceType; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
    
    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }
    
    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }
    
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus preparation) { this.status = preparation; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public BigDecimal getDiscount() { return discount; }
    public void setDiscount(BigDecimal discount) { this.discount = discount; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Therapist getTherapist() { return therapist; }
    public void setTherapist(Therapist therapist) { this.therapist = therapist; }
    
    public User getUser() { return customer; }
    public void setUser(User customer) { this.customer = customer; }

    public BigDecimal getTherapistEarnings() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTherapistEarnings'");
    }

    public BigDecimal getRubhubServiceFee() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRubhubServiceFee'");
    }

    public Object getScheduledTime() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getScheduledTime'");
    }

    public Object getTravelFee() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTravelFee'");
    }

    public void setTravelFee(Object travelFee) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setTravelFee'");
    }

    public Object getPreparationTime() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPreparationTime'");
    }

    public void setScheduledTime(Object scheduledTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setScheduledTime'");
    }

    public void setPreparationTime(Object preparationTime) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPreparationTime'");
    }

    public void setPayoutProcessed(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPayoutProcessed'");
    }

    public void calculateTotals() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'calculateTotals'");
    }

	public Object getCompletedAt() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getCompletedAt'");
	}

    public void setActualStartTime(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setActualStartTime'");
    }

    public void setActualEndTime(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setActualEndTime'");
    }

    public void setRating(Rating rating) {
        this.rating=rating;
    }
}