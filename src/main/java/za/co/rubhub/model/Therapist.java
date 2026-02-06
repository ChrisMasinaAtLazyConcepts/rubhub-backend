package za.co.rubhub.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "therapists")
public class Therapist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @Column(name = "license_number", unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "email", unique = true, length = 50)
    private String email;
    
    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;
    
    @Column(name = "specialization", length = 200)
    private String specialization;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;
    
    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "verification_documents", columnDefinition = "jsonb")
    private String verificationDocuments; // JSON array of document URLs
    
    @Column(name = "available_from")
    private String availableFrom; // e.g., "09:00"
    
    @Column(name = "available_to")
    private String availableTo; // e.g., "17:00"
    
    @Column(name = "working_days", length = 50)
    private String workingDays; // e.g., "Mon,Tue,Wed,Thu,Fri"
    
    @Column(name = "travel_radius")
    private Integer travelRadius; // in kilometers
    
    @Column(name = "has_vehicle")
    private Boolean hasVehicle = false;
    
    @Column(name = "is_travel_ready")
    private Boolean isTravelReady = true;
    
    @Column(name = "equipment", columnDefinition = "jsonb")
    private String equipment; // JSON array of equipment
    
    @Column(name = "certifications", columnDefinition = "jsonb")
    private String certifications; // JSON array of certifications
    
    @Column(name = "languages", columnDefinition = "jsonb")
    private String languages; // JSON array of languages spoken
    
    @Column(name = "profile_completion_percentage")
    private Integer profileCompletionPercentage = 0;
    
    @Column(name = "total_bookings_completed")
    private Integer totalBookingsCompleted = 0;
    
    @Column(name = "total_earnings", precision = 15, scale = 2)
    private BigDecimal totalEarnings = BigDecimal.ZERO;
    
    @Column(name = "cancellation_rate", precision = 5, scale = 2)
    private BigDecimal cancellationRate = BigDecimal.ZERO;
    
    @Column(name = "response_time_minutes")
    private Integer averageResponseTimeMinutes;
    
    @Column(name = "last_active")
    private LocalDateTime lastActive;
    
    @Column(name = "on_call")
    private Boolean onCall = false;
    
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;
    
    @Column(name = "emergency_contact_relationship", length = 50)
    private String emergencyContactRelationship;
    
    @Column(name = "insurance_details", columnDefinition = "jsonb")
    private String insuranceDetails;
    
    @Column(name = "background_check_status", length = 20)
    private String backgroundCheckStatus; // PENDING, APPROVED, REJECTED
    
    @Column(name = "background_check_date")
    private LocalDateTime backgroundCheckDate;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "therapist", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
    
    @OneToMany(mappedBy = "therapist", fetch = FetchType.LAZY)
    private List<MassageRequest> massageRequests = new ArrayList<>();
    
    // Timestamp handlers
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.rating == null) {
            this.rating = BigDecimal.ZERO;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public Therapist() {}
    
    public Therapist(User user) {
        this.user = user;
        this.isAvailable = true;
        this.isVerified = false;
        this.totalReviews = 0;
        this.rating = BigDecimal.ZERO;
        this.totalBookingsCompleted = 0;
        this.totalEarnings = BigDecimal.ZERO;
        this.cancellationRate = BigDecimal.ZERO;
        this.profileCompletionPercentage = 0;
    }
    
    // Business logic methods
    public void addBooking(Booking booking) {
        this.bookings.add(booking);
        this.totalBookingsCompleted++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateRating(BigDecimal newRating) {
        if (this.rating == null) {
            this.rating = newRating;
        } else {
            // Calculate new average rating
            BigDecimal totalRating = this.rating.multiply(new BigDecimal(this.totalReviews));
            totalRating = totalRating.add(newRating);
            this.totalReviews++;
            this.rating = totalRating.divide(new BigDecimal(this.totalReviews), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
    
    public void addEarnings(BigDecimal amount) {
        if (this.totalEarnings == null) {
            this.totalEarnings = BigDecimal.ZERO;
        }
        this.totalEarnings = this.totalEarnings.add(amount);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void calculateProfileCompletion() {
        int completedFields = 0;
        int totalFields = 10; // Adjust based on your required fields
        
        if (licenseNumber != null && !licenseNumber.isEmpty()) completedFields++;
        if (specialization != null && !specialization.isEmpty()) completedFields++;
        if (bio != null && !bio.isEmpty()) completedFields++;
        if (hourlyRate != null) completedFields++;
        if (availableFrom != null && !availableFrom.isEmpty()) completedFields++;
        if (availableTo != null && !availableTo.isEmpty()) completedFields++;
        if (workingDays != null && !workingDays.isEmpty()) completedFields++;
        if (travelRadius != null) completedFields++;
        if (certifications != null && !certifications.isEmpty()) completedFields++;
        if (languages != null && !languages.isEmpty()) completedFields++;
        
        this.profileCompletionPercentage = (completedFields * 100) / totalFields;
    }
    
    public boolean isFullyVerified() {
        return isVerified && 
               "APPROVED".equals(backgroundCheckStatus) &&
               profileCompletionPercentage >= 80;
    }
    
    // Getter for PayFast beneficiary ID
    public String getPayfastBeneficiaryId() {
        // Generate or retrieve PayFast beneficiary ID
        if (this.id != null) {
            return "RUBHUB-THERAPIST-" + this.id;
        }
        return null;
    }
    
    // Getters and Setters for ALL fields
    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public User getUser() { 
        return user; 
    }
    
    public void setUser(User user) { 
        this.user = user; 
    }
    
    public String getLicenseNumber() { 
        return licenseNumber; 
    }
    
    public void setLicenseNumber(String licenseNumber) { 
        this.licenseNumber = licenseNumber; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public Integer getYearsOfExperience() { 
        return yearsOfExperience; 
    }
    
    public void setYearsOfExperience(Integer yearsOfExperience) { 
        this.yearsOfExperience = yearsOfExperience; 
    }
    
    public String getSpecialization() { 
        return specialization; 
    }
    
    public void setSpecialization(String specialization) { 
        this.specialization = specialization; 
    }
    
    public String getBio() { 
        return bio; 
    }
    
    public void setBio(String bio) { 
        this.bio = bio; 
    }
    
    public BigDecimal getHourlyRate() { 
        return hourlyRate; 
    }
    
    public void setHourlyRate(BigDecimal hourlyRate) { 
        this.hourlyRate = hourlyRate; 
    }
    
    public BigDecimal getRating() { 
        return rating; 
    }
    
    public void setRating(BigDecimal rating) { 
        this.rating = rating; 
    }
    
    public Integer getTotalReviews() { 
        return totalReviews; 
    }
    
    public void setTotalReviews(Integer totalReviews) { 
        this.totalReviews = totalReviews; 
    }
    
    public Boolean getIsAvailable() { 
        return isAvailable; 
    }
    
    public void setIsAvailable(Boolean isAvailable) { 
        this.isAvailable = isAvailable; 
    }
    
    public Boolean getIsVerified() { 
        return isVerified; 
    }
    
    public void setIsVerified(Boolean isVerified) { 
        this.isVerified = isVerified; 
    }
    
    public String getVerificationDocuments() { 
        return verificationDocuments; 
    }
    
    public void setVerificationDocuments(String verificationDocuments) { 
        this.verificationDocuments = verificationDocuments; 
    }
    
    public String getAvailableFrom() { 
        return availableFrom; 
    }
    
    public void setAvailableFrom(String availableFrom) { 
        this.availableFrom = availableFrom; 
    }
    
    public String getAvailableTo() { 
        return availableTo; 
    }
    
    public void setAvailableTo(String availableTo) { 
        this.availableTo = availableTo; 
    }
    
    public String getWorkingDays() { 
        return workingDays; 
    }
    
    public void setWorkingDays(String workingDays) { 
        this.workingDays = workingDays; 
    }
    
    public Integer getTravelRadius() { 
        return travelRadius; 
    }
    
    public void setTravelRadius(Integer travelRadius) { 
        this.travelRadius = travelRadius; 
    }
    
    public Boolean getHasVehicle() { 
        return hasVehicle; 
    }
    
    public void setHasVehicle(Boolean hasVehicle) { 
        this.hasVehicle = hasVehicle; 
    }
    
    public Boolean getIsTravelReady() { 
        return isTravelReady; 
    }
    
    public void setIsTravelReady(Boolean isTravelReady) { 
        this.isTravelReady = isTravelReady; 
    }
    
    public String getEquipment() { 
        return equipment; 
    }
    
    public void setEquipment(String equipment) { 
        this.equipment = equipment; 
    }
    
    public String getCertifications() { 
        return certifications; 
    }
    
    public void setCertifications(String certifications) { 
        this.certifications = certifications; 
    }
    
    public String getLanguages() { 
        return languages; 
    }
    
    public void setLanguages(String languages) { 
        this.languages = languages; 
    }
    
    public Integer getProfileCompletionPercentage() { 
        return profileCompletionPercentage; 
    }
    
    public void setProfileCompletionPercentage(Integer profileCompletionPercentage) { 
        this.profileCompletionPercentage = profileCompletionPercentage; 
    }
    
    public Integer getTotalBookingsCompleted() { 
        return totalBookingsCompleted; 
    }
    
    public void setTotalBookingsCompleted(Integer totalBookingsCompleted) { 
        this.totalBookingsCompleted = totalBookingsCompleted; 
    }
    
    public BigDecimal getTotalEarnings() { 
        return totalEarnings; 
    }
    
    public void setTotalEarnings(BigDecimal totalEarnings) { 
        this.totalEarnings = totalEarnings; 
    }
    
    public BigDecimal getCancellationRate() { 
        return cancellationRate; 
    }
    
    public void setCancellationRate(BigDecimal cancellationRate) { 
        this.cancellationRate = cancellationRate; 
    }
    
    public Integer getAverageResponseTimeMinutes() { 
        return averageResponseTimeMinutes; 
    }
    
    public void setAverageResponseTimeMinutes(Integer averageResponseTimeMinutes) { 
        this.averageResponseTimeMinutes = averageResponseTimeMinutes; 
    }
    
    public LocalDateTime getLastActive() { 
        return lastActive; 
    }
    
    public void setLastActive(LocalDateTime lastActive) { 
        this.lastActive = lastActive; 
    }
    
    public Boolean getOnCall() { 
        return onCall; 
    }
    
    public void setOnCall(Boolean onCall) { 
        this.onCall = onCall; 
    }
    
    public String getEmergencyContactName() { 
        return emergencyContactName; 
    }
    
    public void setEmergencyContactName(String emergencyContactName) { 
        this.emergencyContactName = emergencyContactName; 
    }
    
    public String getEmergencyContactPhone() { 
        return emergencyContactPhone; 
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) { 
        this.emergencyContactPhone = emergencyContactPhone; 
    }
    
    public String getEmergencyContactRelationship() { 
        return emergencyContactRelationship; 
    }
    
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { 
        this.emergencyContactRelationship = emergencyContactRelationship; 
    }
    
    public String getInsuranceDetails() { 
        return insuranceDetails; 
    }
    
    public void setInsuranceDetails(String insuranceDetails) { 
        this.insuranceDetails = insuranceDetails; 
    }
    
    public String getBackgroundCheckStatus() { 
        return backgroundCheckStatus; 
    }
    
    public void setBackgroundCheckStatus(String backgroundCheckStatus) { 
        this.backgroundCheckStatus = backgroundCheckStatus; 
    }
    
    public LocalDateTime getBackgroundCheckDate() { 
        return backgroundCheckDate; 
    }
    
    public void setBackgroundCheckDate(LocalDateTime backgroundCheckDate) { 
        this.backgroundCheckDate = backgroundCheckDate; 
    }
    
    public String getNotes() { 
        return notes; 
    }
    
    public void setNotes(String notes) { 
        this.notes = notes; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }
    
    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
    
    public List<Booking> getBookings() { 
        return bookings; 
    }
    
    public void setBookings(List<Booking> bookings) { 
        this.bookings = bookings; 
    }
    
    public List<MassageRequest> getMassageRequests() { 
        return massageRequests; 
    }
    
    public void setMassageRequests(List<MassageRequest> massageRequests) { 
        this.massageRequests = massageRequests; 
    }
    
    // Helper methods for boolean fields (alternative naming)
    public Boolean isAvailable() { 
        return isAvailable; 
    }
    
    public Boolean isVerified() { 
        return isVerified; 
    }
    
    public Boolean hasVehicle() { 
        return hasVehicle; 
    }
    
    public Boolean isTravelReady() { 
        return isTravelReady; 
    }
    
    public Boolean isOnCall() { 
        return onCall; 
    }
    
    // Convenience methods for JSON fields
    public void addCertification(String certification) {
        // TODO: Implement JSON array manipulation
    }
    
    public void addLanguage(String language) {
        // TODO: Implement JSON array manipulation
    }
    
    public void addEquipment(String equipmentItem) {
        // TODO: Implement JSON array manipulation
    }

    // Add this method to your Therapist.java model
public boolean isAvailableForBooking() {
    // Check basic availability
    if (!Boolean.TRUE.equals(this.isAvailable)) {
        return false;
    }
    
    // Check if therapist is verified
    if (!Boolean.TRUE.equals(this.isVerified)) {
        return false;
    }
    
    // Check if therapist is on call (if onCall is required)
    if (this.onCall != null && !Boolean.TRUE.equals(this.onCall)) {
        return false;
    }
    
    // Check if therapist has working hours set
    if (this.availableFrom == null || this.availableTo == null) {
        return false;
    }
    
    // Check if within working hours
    LocalTime now = LocalTime.now();
    try {
        LocalTime start = LocalTime.parse(this.availableFrom);
        LocalTime end = LocalTime.parse(this.availableTo);
        
        if (now.isBefore(start) || now.isAfter(end)) {
            return false;
        }
    } catch (Exception e) {
        // If time parsing fails, assume available
    }
    
    // Check if today is a working day
    if (this.workingDays != null && !this.workingDays.isEmpty()) {
        String today = DayOfWeek.from(LocalDate.now()).toString().substring(0, 3);
        if (!this.workingDays.contains(today)) {
            return false;
        }
    }
    
    // Check background check status
    if (!"APPROVED".equals(this.backgroundCheckStatus)) {
        return false;
    }
    
    // Check profile completion
    if (this.profileCompletionPercentage == null || this.profileCompletionPercentage < 80) {
        return false;
    }
    
    // Check if therapist is travel ready if they need to travel
    if (this.travelRadius != null && this.travelRadius > 0 && 
        !Boolean.TRUE.equals(this.isTravelReady)) {
        return false;
    }
    
    // All checks passed
    return true;
}

public void verifyTherapist() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'verifyTherapist'");
}

public void rejectTherapist(String reason) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'rejectTherapist'");
}

public int getCompletedSessions() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCompletedSessions'");
}

public void setCompletedSessions(int i) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'setCompletedSessions'");
}
}