package za.co.rubhub.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "massage_service_types")
public class MassageServiceType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Change from String to Long
    
    @Column(name = "service_code", unique = true, nullable = false, length = 10)
    private String serviceCode; // Add this field for unique code
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "duration", nullable = false)
    private Integer duration; // in minutes
    
    @Column(name = "is_active", nullable = false)
    private boolean active = true; 
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // Using BigDecimal for monetary values
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private ServiceCategory category;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;
    
    public enum ServiceCategory {
        RELAXATION, 
        THERAPEUTIC, 
        SPECIALIZED
    }
    
    // Constructors
    public MassageServiceType() {}
    
    public MassageServiceType(String serviceCode, String name, Integer duration, 
                         BigDecimal price, String description, ServiceCategory category) {
        this.serviceCode = serviceCode;
        this.name = name;
        this.duration = duration;
        this.price = price;
        this.description = description;
        this.category = category;
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    // Helper setter for Double
    public void setPrice(Double price) { 
        this.price = price != null ? BigDecimal.valueOf(price) : null; 
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ServiceCategory getCategory() { return category; }
    public void setCategory(ServiceCategory category) { this.category = category; }
    
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.time.LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}