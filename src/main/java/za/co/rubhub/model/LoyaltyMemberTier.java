package za.co.rubhub.model;

import lombok.*;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_member_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyMemberTier {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private LoyaltyProgram loyaltyProgram;
    
    @Column(name = "current_points")
    @Builder.Default
    private Integer currentPoints = 0;
    
    @Column(name = "lifetime_points")
    @Builder.Default
    private Integer lifetimePoints = 0;
    
    @Column(name = "total_spent", precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;
    
    @Column(name = "current_tier", length = 50)
    @Builder.Default
    private String currentTier = "Bronze";
    
    @Column(name = "referral_code", length = 20, unique = true)
    private String referralCode;
    
    @Column(name = "referrals_count")
    @Builder.Default
    private Integer referralsCount = 0;
    
    @Column(name = "free_sessions_available")
    @Builder.Default
    private Integer freeSessionsAvailable = 0;
    
    @Column(name = "last_points_earned_at")
    private LocalDateTime lastPointsEarnedAt;
    
    @Column(name = "tier_upgraded_at")
    private LocalDateTime tierUpgradedAt;
    
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
}