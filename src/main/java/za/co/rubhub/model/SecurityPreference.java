package za.co.rubhub.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import za.co.rubhub.model.*;

@Entity
@Table(name = "security_preferences")
public class SecurityPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "user_type", nullable = false, length = 20)
    private String userType; // customer, therapist
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "security_preference_id")
    private List<EmergencyContact> emergencyContacts;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "security_preference_id")
    private List<SecurityCompany> securityCompanies;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
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
    public SecurityPreference() {}

    public SecurityPreference(User user, String userType) {
        this.user = user;
        this.userType = userType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getUserType() { 
        return userType; 
    }
    
    public void setUserType(String userType) { 
        this.userType = userType; 
    }

    public List<EmergencyContact> getEmergencyContacts() { 
        return emergencyContacts; 
    }
    
    public void setEmergencyContacts(List<EmergencyContact> emergencyContacts) { 
        this.emergencyContacts = emergencyContacts; 
    }

    public List<SecurityCompany> getSecurityCompanies() { 
        return securityCompanies; 
    }
    
    public void setSecurityCompanies(List<SecurityCompany> securityCompanies) { 
        this.securityCompanies = securityCompanies; 
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
}