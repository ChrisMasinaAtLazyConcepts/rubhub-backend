package za.co.rubhub.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    
    @Column(name = "first_name", nullable = false, length = 50)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    @Column(name = "last_name", nullable = false, length = 50)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    @Column(name = "phone", nullable = false, length = 20)
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone format")
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    @NotNull(message = "User type is required")
    private UserType userType;
    
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
    
    @Column(name = "date_of_birth")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @Embedded
    @NotNull(message = "Address is required")
    private Address address;
    
    // Loyalty Program Fields
    @Column(name = "loyalty_points")
    @Min(value = 0, message = "Loyalty points cannot be negative")
    @Builder.Default
    private Integer loyaltyPoints = 0;
    
    @Column(name = "credits", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Credits cannot be negative")
    @Builder.Default
    private BigDecimal credits = BigDecimal.ZERO;
    
    @Column(name = "free_massages_available")
    @Min(value = 0, message = "Free massages available cannot be negative")
    @Builder.Default
    private Integer freeMassagesAvailable = 0;
    
    @Column(name = "referral_code", unique = true, length = 50)
    private String referralCode;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_by_id")
    private User referredBy;
    
    @Column(name = "total_referrals")
    @Min(value = 0, message = "Total referrals cannot be negative")
    @Builder.Default
    private Integer totalReferrals = 0;
    
    @Column(name = "referral_earnings", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Referral earnings cannot be negative")
    @Builder.Default
    private BigDecimal referralEarnings = BigDecimal.ZERO;
    
    // Security Fields
    @Column(name = "two_factor_enabled")
    @Builder.Default
    private Boolean twoFactorEnabled = false;
    
    @Column(name = "two_factor_secret", length = 100)
    private String twoFactorSecret;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "login_attempts")
    @Min(value = 0, message = "Login attempts cannot be negative")
    @Builder.Default
    private Integer loginAttempts = 0;
    
    @Column(name = "lock_until")
    private LocalDateTime lockUntil;
    
    // Status Management
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    
    @Column(name = "flagged_reason", length = 500)
    private String flaggedReason;
    
    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;
    
    @Column(name = "verification_token", length = 100)
    private String verificationToken;
    
    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;
    
    @Column(name = "password_reset_token", length = 100)
    private String passwordResetToken;
    
    @Column(name = "password_reset_expiry")
    private LocalDateTime passwordResetExpiry;
    
    // Preferences
    @Column(name = "preferences", columnDefinition = "jsonb")
    private String preferences; // JSON for user preferences
    
    @Column(name = "notification_settings", columnDefinition = "jsonb")
    private String notificationSettings; // JSON for notification preferences
    
    // Additional personal information
    @Column(name = "gender", length = 10)
    private String gender;
    
    @Column(name = "occupation", length = 100)
    private String occupation;
    
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;
    
    @Column(name = "emergency_contact_relationship", length = 50)
    private String emergencyContactRelationship;
    
    // Statistics
    @Column(name = "total_bookings")
    @Builder.Default
    private Integer totalBookings = 0;
    
    @Column(name = "total_spent", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;
    
    @Column(name = "last_booking_date")
    private LocalDateTime lastBookingDate;
    
    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;
    
    @Column(name = "phone_verified_at")
    private LocalDateTime phoneVerifiedAt;
    
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
        
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Therapist therapist;

 @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
private List<MassageRequest> massageRequests = new ArrayList<>();
    
    // Enums
    public enum UserType {
        CUSTOMER, THERAPIST, ADMIN, SUPPORT, MANAGER
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE, FLAGGED, BANNED, SUSPENDED, DELETED
    }
    
    // Embeddable classes
    @Embeddable
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Address {
        
        @Column(name = "street_address", nullable = false)
        @NotBlank(message = "Street address is required")
        private String streetAddress;
        
        @Column(name = "apartment_number", length = 50)
        private String apartmentNumber;
        
        @Column(name = "city", nullable = false, length = 100)
        @NotBlank(message = "City is required")
        private String city;
        
        @Column(name = "state", nullable = false, length = 100)
        @NotBlank(message = "State is required")
        private String state;
        
        @Column(name = "postal_code", nullable = false, length = 20)
        @NotBlank(message = "Postal code is required")
        private String postalCode;
        
        @Column(name = "country", nullable = false, length = 2)
        @NotBlank(message = "Country is required")
        private String country;
        
        @Column(name = "latitude")
        private Double latitude;
        
        @Column(name = "longitude")
        private Double longitude;
        
        @Column(name = "is_primary", nullable = false)
        @Builder.Default
        private Boolean isPrimary = true;
        
        @Column(name = "address_label", length = 50)
        private String label; // Home, Work, Other
    }
    
    // Business logic methods
    @PrePersist
    protected void onCreate() {
        if (referralCode == null) {
            referralCode = generateReferralCode();
        }
        if (phone == null) {
            phone = phoneNumber;
        }
    }
    
    private String generateReferralCode() {
        return "REF-" + System.currentTimeMillis() + 
               (int)(Math.random() * 1000);
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public void addLoyaltyPoints(Integer points) {
        if (points > 0) {
            this.loyaltyPoints += points;
        }
    }
    
    public void addCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.credits = this.credits.add(amount);
        }
    }
    
    public void deductCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && 
            this.credits.compareTo(amount) >= 0) {
            this.credits = this.credits.subtract(amount);
        }
    }
    
    public void incrementTotalBookings() {
        this.totalBookings++;
        this.lastBookingDate = LocalDateTime.now();
    }
    
    public void addToTotalSpent(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.totalSpent = this.totalSpent.add(amount);
        }
    }
    
    public void verifyEmail() {
        this.isVerified = true;
        this.emailVerifiedAt = LocalDateTime.now();
        this.verificationToken = null;
        this.verificationTokenExpiry = null;
    }
    
    public void verifyPhone() {
        this.phoneVerifiedAt = LocalDateTime.now();
    }
    
    public boolean isLocked() {
        return lockUntil != null && lockUntil.isAfter(LocalDateTime.now());
    }
    
    public void lockAccount(Integer minutes) {
        this.lockUntil = LocalDateTime.now().plusMinutes(minutes);
    }
    
    public void unlockAccount() {
        this.lockUntil = null;
        this.loginAttempts = 0;
    }
    
    public void incrementLoginAttempts() {
        this.loginAttempts++;
        if (this.loginAttempts >= 5) {
            lockAccount(30); // Lock for 30 minutes after 5 failed attempts
        }
    }
    
    public void resetLoginAttempts() {
        this.loginAttempts = 0;
        this.lockUntil = null;
    }
    
    public boolean isAdmin() {
        return userType == UserType.ADMIN || userType == UserType.MANAGER;
    }
    
    public boolean isTherapist() {
        return userType == UserType.THERAPIST;
    }
    
    public boolean isCustomer() {
        return userType == UserType.CUSTOMER;
    }
    
    public String getFormattedTotalSpent() {
        return String.format("R %.2f", totalSpent != null ? totalSpent : BigDecimal.ZERO);
    }
    
    // Indexes for better query performance
    @Table(name = "users", indexes = {
        @Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_phone_number", columnList = "phone_number", unique = true),
        @Index(name = "idx_user_phone", columnList = "phone"),
        @Index(name = "idx_user_user_type", columnList = "user_type"),
        @Index(name = "idx_user_status", columnList = "status"),
        @Index(name = "idx_user_referral_code", columnList = "referral_code", unique = true),
        @Index(name = "idx_user_referred_by", columnList = "referred_by_id"),
        @Index(name = "idx_user_city", columnList = "city"),
        @Index(name = "idx_user_created_at", columnList = "created_at"),
        @Index(name = "idx_user_last_login", columnList = "last_login"),
        @Index(name = "idx_user_is_verified", columnList = "is_verified")
    })
    static class UserTableIndices {}
}