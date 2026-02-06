package za.co.rubhub.repositories;

import za.co.rubhub.model.SecurityAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityAlertRepository extends JpaRepository<SecurityAlert, Long> {
    
    // Basic queries
    List<SecurityAlert> findByStatus(SecurityAlert.AlertStatus status);
    List<SecurityAlert> findByAlertType(SecurityAlert.AlertType type);
    List<SecurityAlert> findByIsFlashingTrue();
    List<SecurityAlert> findByStreamAvailableTrue();
    
    // User and booking related
    List<SecurityAlert> findByUserId(String userId);
    List<SecurityAlert> findByRequestId(String requestId);
    
    // Date range queries
    List<SecurityAlert> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<SecurityAlert> findByResolvedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Priority and status combinations
    List<SecurityAlert> findByStatusAndPriorityGreaterThanEqual(
        SecurityAlert.AlertStatus status, Integer priority);
    
    // Geographic queries - Using Location embeddable
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.location.city = :city AND sa.status = 'ACTIVE'")
    List<SecurityAlert> findActiveAlertsByCity(@Param("city") String city);
    
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.location.suburb = :suburb " +
           "AND sa.status IN ('ACTIVE', 'IN_PROGRESS')")
    List<SecurityAlert> findActiveAlertsBySuburb(@Param("suburb") String suburb);
    
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.location.latitude BETWEEN :minLat AND :maxLat " +
           "AND sa.location.longitude BETWEEN :minLng AND :maxLng " +
           "AND sa.status = 'ACTIVE'")
    List<SecurityAlert> findActiveAlertsInArea(@Param("minLat") Double minLat, 
                                               @Param("maxLat") Double maxLat,
                                               @Param("minLng") Double minLng,
                                               @Param("maxLng") Double maxLng);
    
    // Notification status queries - Using NotificationStatus embeddable
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.notificationStatus.sapsNotified = false " +
           "AND sa.status = 'ACTIVE'")
    List<SecurityAlert> findActiveAlertsWithoutSAPSNotification();
    
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.notificationStatus.securityCompanyNotified = false " +
           "AND sa.status = 'ACTIVE'")
    List<SecurityAlert> findActiveAlertsWithoutSecurityCompanyNotification();
    
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.notificationStatus.emergencyContactsNotified = false " +
           "AND sa.status = 'ACTIVE'")
    List<SecurityAlert> findActiveAlertsWithoutEmergencyContactsNotification();
    
    // Count queries
    long countByStatus(SecurityAlert.AlertStatus status);
    
    @Query("SELECT COUNT(sa) FROM SecurityAlert sa WHERE sa.alertType = :type " +
           "AND sa.timestamp BETWEEN :start AND :end")
    long countByAlertTypeAndTimestampBetween(@Param("type") SecurityAlert.AlertType type,
                                           @Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end);
    
    long countByIsFlashingTrue();
    
    // Resolution time analysis
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.status = 'RESOLVED' " +
           "AND sa.resolvedAt IS NOT NULL")
    List<SecurityAlert> findResolvedAlertsWithTimestamps();
    
    // Find alerts that need attention (high priority and not resolved)
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.priority >= :minPriority " +
           "AND sa.status IN ('ACTIVE', 'IN_PROGRESS') " +
           "ORDER BY sa.priority DESC, sa.timestamp ASC")
    List<SecurityAlert> findHighPriorityAlerts(@Param("minPriority") Integer minPriority);
    
    // Find unresolved alerts older than specified time
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.status != 'RESOLVED' " +
           "AND sa.timestamp < :olderThan")
    List<SecurityAlert> findUnresolvedAlertsOlderThan(@Param("olderThan") LocalDateTime olderThan);
    
    // Find alerts by multiple statuses
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.status IN :statuses " +
           "ORDER BY sa.timestamp DESC")
    List<SecurityAlert> findByStatusIn(@Param("statuses") List<SecurityAlert.AlertStatus> statuses);
   
    // Statistics queries
    @Query("SELECT sa.alertType, COUNT(sa), AVG(sa.resolutionTimeMinutes) " +
           "FROM SecurityAlert sa WHERE sa.status = 'RESOLVED' " +
           "AND sa.resolvedAt IS NOT NULL " +
           "GROUP BY sa.alertType")
    List<Object[]> getAlertTypeStatistics();
    
    @Query("SELECT DATE(sa.timestamp), COUNT(sa), COUNT(CASE WHEN sa.status = 'RESOLVED' THEN 1 END) " +
           "FROM SecurityAlert sa " +
           "WHERE sa.timestamp BETWEEN :start AND :end " +
           "GROUP BY DATE(sa.timestamp) " +
           "ORDER BY DATE(sa.timestamp) DESC")
    List<Object[]> getDailyAlertStats(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
    
    // Find similar/duplicate alerts (same location and time window)
    @Query("SELECT sa FROM SecurityAlert sa WHERE " +
           "sa.location.latitude BETWEEN :lat - 0.01 AND :lat + 0.01 " +
           "AND sa.location.longitude BETWEEN :lng - 0.01 AND :lng + 0.01 " +
           "AND sa.timestamp BETWEEN :startTime AND :endTime " +
           "AND sa.id != :excludeId " +
           "AND sa.status != 'RESOLVED'")
    List<SecurityAlert> findSimilarAlerts(@Param("lat") Double latitude,
                                         @Param("lng") Double longitude,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("excludeId") Long excludeId);
    
    // Pagination queries
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.status = :status " +
           "ORDER BY sa.timestamp DESC")
    List<SecurityAlert> findByStatusWithPagination(@Param("status") SecurityAlert.AlertStatus status,
                                                  org.springframework.data.domain.Pageable pageable);
    
    // Search by description or notes
    @Query("SELECT sa FROM SecurityAlert sa WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(sa.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(sa.notes) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(sa.location.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY sa.timestamp DESC")
    List<SecurityAlert> searchAlerts(@Param("searchTerm") String searchTerm,
                                    org.springframework.data.domain.Pageable pageable);
    
    // Find alerts for a specific therapist
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.therapist.id = :therapistId " +
           "ORDER BY sa.timestamp DESC")
    List<SecurityAlert> findByTherapistId(@Param("therapistId") Long therapistId);
    
    // Find alerts for a specific booking
    @Query("SELECT sa FROM SecurityAlert sa WHERE sa.booking.id = :bookingId")
    Optional<SecurityAlert> findByBookingId(@Param("bookingId") Long bookingId);
    
    // Update methods
    @Query("UPDATE SecurityAlert sa SET sa.status = :status, sa.resolvedAt = :resolvedAt " +
           "WHERE sa.id = :alertId")
    int updateAlertStatus(@Param("alertId") Long alertId,
                         @Param("status") SecurityAlert.AlertStatus status,
                         @Param("resolvedAt") LocalDateTime resolvedAt);
    
    @Query("UPDATE SecurityAlert sa SET sa.notificationStatus.sapsNotified = true, " +
           "sa.notificationStatus.sapsNotificationTime = CURRENT_TIMESTAMP " +
           "WHERE sa.id = :alertId")
    int markSAPSNotified(@Param("alertId") Long alertId);
    
    @Query("UPDATE SecurityAlert sa SET sa.notificationStatus.securityCompanyNotified = true, " +
           "sa.notificationStatus.securityCompanyNotificationTime = CURRENT_TIMESTAMP " +
           "WHERE sa.id = :alertId")
    int markSecurityCompanyNotified(@Param("alertId") Long alertId);
    
    // Cleanup/archive old resolved alerts
    @Query("DELETE FROM SecurityAlert sa WHERE sa.status = 'RESOLVED' " +
           "AND sa.resolvedAt < :olderThan")
    int deleteOldResolvedAlerts(@Param("olderThan") LocalDateTime olderThan);
    Optional<SecurityAlert> findLatestByTimeRange(String timeRange, LocalDateTime minusHours);
}