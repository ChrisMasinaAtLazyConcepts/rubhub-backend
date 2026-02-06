package za.co.rubhub.repositories;

import za.co.rubhub.model.LoyaltyMemberTier;
import za.co.rubhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyMemberTierRepository extends JpaRepository<LoyaltyMemberTier, Long> {
    
    Optional<LoyaltyMemberTier> findByUser(User user);
    
    Optional<LoyaltyMemberTier> findByUserId(Long userId);
    
    Optional<LoyaltyMemberTier> findByUserAndLoyaltyProgram(User user, za.co.rubhub.model.LoyaltyProgram loyaltyProgram);
    
    List<LoyaltyMemberTier> findByLoyaltyProgramId(Long programId);
    
    List<LoyaltyMemberTier> findByCurrentTier(String tier);
    
    List<LoyaltyMemberTier> findByCurrentPointsGreaterThanEqual(Integer points);
    
    List<LoyaltyMemberTier> findByTotalSpentGreaterThanEqual(BigDecimal amount);
    
    Optional<LoyaltyMemberTier> findByReferralCode(String referralCode);
    
    @Query("SELECT lmt FROM LoyaltyMemberTier lmt WHERE lmt.user.id = :userId AND lmt.loyaltyProgram.id = :programId")
    Optional<LoyaltyMemberTier> findByUserIdAndProgramId(@Param("userId") Long userId, @Param("programId") Long programId);
    
    @Query("SELECT lmt FROM LoyaltyMemberTier lmt WHERE lmt.loyaltyProgram.isActive = true AND lmt.currentPoints >= lmt.loyaltyProgram.rules.freeMassageAfterPoints")
    List<LoyaltyMemberTier> findMembersEligibleForFreeMassage();
    
    @Query("SELECT lmt FROM LoyaltyMemberTier lmt WHERE lmt.loyaltyProgram.isActive = true AND " +
           "lmt.referralsCount > 0 AND lmt.loyaltyProgram.type = 'referral'")
    List<LoyaltyMemberTier> findMembersWithReferrals();
    
    @Query("SELECT lmt.currentTier, COUNT(lmt) FROM LoyaltyMemberTier lmt WHERE lmt.loyaltyProgram.id = :programId GROUP BY lmt.currentTier")
    List<Object[]> countMembersByTier(@Param("programId") Long programId);
    
    @Query("SELECT SUM(lmt.currentPoints) FROM LoyaltyMemberTier lmt WHERE lmt.loyaltyProgram.id = :programId")
    Long getTotalPointsInProgram(@Param("programId") Long programId);
    
    @Query("SELECT SUM(lmt.totalSpent) FROM LoyaltyMemberTier lmt WHERE lmt.loyaltyProgram.id = :programId")
    BigDecimal getTotalRevenueFromProgram(@Param("programId") Long programId);
    
    @Query("SELECT lmt.user.id, lmt.currentPoints, lmt.currentTier FROM LoyaltyMemberTier lmt " +
           "WHERE lmt.loyaltyProgram.id = :programId ORDER BY lmt.currentPoints DESC")
    List<Object[]> getLeaderboard(@Param("programId") Long programId);
    
    @Query("SELECT COUNT(lmt) FROM LoyaltyMemberTier lmt WHERE lmt.loyaltyProgram.id = :programId")
    Long countMembersInProgram(@Param("programId") Long programId);
    
    boolean existsByUser(User user);
    
    boolean existsByUserId(Long userId);
    
    boolean existsByReferralCode(String referralCode);
}