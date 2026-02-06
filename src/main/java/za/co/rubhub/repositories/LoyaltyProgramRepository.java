package za.co.rubhub.repositories;

import za.co.rubhub.model.LoyaltyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {
    
    Optional<LoyaltyProgram> findByName(String name);
    
    List<LoyaltyProgram> findByType(String type);
    
    List<LoyaltyProgram> findByIsActiveTrue();
    
    List<LoyaltyProgram> findByTypeAndIsActiveTrue(String type);
    
    @Query("SELECT lp FROM LoyaltyProgram lp WHERE lp.isActive = true AND " +
           "(lp.startDate IS NULL OR lp.startDate <= :currentDate) AND " +
           "(lp.endDate IS NULL OR lp.endDate >= :currentDate)")
    List<LoyaltyProgram> findActivePrograms(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT lp FROM LoyaltyProgram lp WHERE lp.isActive = true AND lp.type = 'referral' AND " +
           "(lp.startDate IS NULL OR lp.startDate <= :currentDate) AND " +
           "(lp.endDate IS NULL OR lp.endDate >= :currentDate)")
    List<LoyaltyProgram> findActiveReferralPrograms(@Param("currentDate") LocalDateTime currentDate);
    
    @Query("SELECT lp FROM LoyaltyProgram lp WHERE lp.rules.signupBonus > 0 AND lp.isActive = true")
    List<LoyaltyProgram> findProgramsWithSignupBonus();
    
    @Query("SELECT lp FROM LoyaltyProgram lp WHERE lp.rules.freeMassageAfterPoints > 0 AND lp.isActive = true")
    List<LoyaltyProgram> findProgramsWithFreeMassageReward();
    
    @Query("SELECT COUNT(lp) FROM LoyaltyProgram lp WHERE lp.isActive = true")
    Long countActivePrograms();
    
    @Query("SELECT lp.type, COUNT(lp) FROM LoyaltyProgram lp WHERE lp.isActive = true GROUP BY lp.type")
    List<Object[]> countActiveProgramsByType();
    
    @Query("SELECT lp FROM LoyaltyProgram lp WHERE lp.createdAt >= :since")
    List<LoyaltyProgram> findProgramsCreatedAfter(@Param("since") LocalDateTime since);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndType(String name, String type);
}