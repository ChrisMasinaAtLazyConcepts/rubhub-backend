package za.co.rubhub.repositories;

import za.co.rubhub.model.Therapist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TherapistRepository extends JpaRepository<Therapist, Long> {
    
    Optional<Therapist> findByUserId(Long userId);
     Optional<Therapist> findByTherapistId(Long therapistId);
     Optional<Therapist> findByEmail(String email);
     List<Therapist> findByVerificationStatus(String status);
    List<Therapist> findByIsAvailableTrue();
    
    List<Therapist> findByIsVerifiedTrue();
    
    List<Therapist> findBySpecializationsContaining(String specialization);
    
    List<Therapist> findByRatingGreaterThanEqual(Double minRating);
    
    List<Therapist> findByHourlyRateBetween(Double minRate, Double maxRate);
    
    @Query("SELECT t FROM Therapist t WHERE t.isActive = true AND t.isAvailable = true AND t.isVerified = true")
    List<Therapist> findAvailableVerifiedTherapists();
    
    @Query("SELECT t FROM Therapist t WHERE t.user.id = :userId")
    Optional<Therapist> findByUser_Id(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Therapist t WHERE t.id IN (SELECT DISTINCT b.therapist.id FROM Booking b WHERE b.serviceType = :serviceType)")
    List<Therapist> findByServiceType(@Param("serviceType") String serviceType);
    
    @Query(value = "SELECT t.* FROM therapists t " +
                   "JOIN users u ON t.user_id = u.id " +
                   "WHERE u.city = :city AND t.is_active = true AND t.is_available = true", nativeQuery = true)
    List<Therapist> findByCityAndAvailable(@Param("city") String city);
    
    @Query("SELECT AVG(t.rating) FROM Therapist t WHERE t.isActive = true")
    Optional<Double> getAverageRating();
    
    @Query("SELECT COUNT(t) FROM Therapist t WHERE t.isActive = true")
    Long countActiveTherapists();
    
    @Query("SELECT t FROM Therapist t WHERE t.lastActiveAt >= :since")
    List<Therapist> findRecentlyActive(@Param("since") LocalDateTime since);
    
    @Query(value = "SELECT t.specialization, COUNT(*) as count " +
                   "FROM therapists t " +
                   "WHERE t.is_active = true " +
                   "GROUP BY t.specialization", nativeQuery = true)
    List<Object[]> getTherapistCountBySpecialization();
    List<Therapist> findByLocationNear(double longitude, double latitude, double maxDistance);
}