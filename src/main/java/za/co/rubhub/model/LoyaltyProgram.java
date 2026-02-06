package za.co.rubhub.model;

import lombok.*;
import javax.persistence.*;

import com.fasterxml.jackson.databind.util.ArrayBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loyalty_programs")
@Getter
@Setter
@ToString(exclude = "memberTiers")
public class LoyaltyProgram {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;
    
    @Column(name = "type", length = 20, nullable = false)
    private String type; // points, tiers, referral, hybrid
    
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @OneToMany(mappedBy = "loyaltyProgram", cascade = CascadeType.ALL)
    private List<LoyaltyMemberTier> memberTiers = new ArrayList();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


    @Embeddable
    @Data
    @AllArgsConstructor
    public static class TierRequirement {
        @Column(name = "tier_name", length = 50)
        private String tier;
        
        @Column(name = "min_points")
        private Integer minPoints;
        
        @Column(name = "min_spent", precision = 19, scale = 4)
        private java.math.BigDecimal minSpent;
        
        @ElementCollection
        @CollectionTable(name = "tier_benefits", joinColumns = @JoinColumn(name = "program_id"))
        @Column(name = "benefit")
        private List<String> benefits = new ArrayList<>();
    }
}