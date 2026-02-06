package za.co.rubhub.repositories;

import za.co.rubhub.model.MassageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MassageRequestRepository extends JpaRepository<MassageRequest, Long> {
    
    // Basic queries
    List<MassageRequest> findByCustomerId(Long customerId);
    List<MassageRequest> findByTherapistId(Long therapistId);
    List<MassageRequest> findByStatus(String status);
    List<MassageRequest> findByPaymentStatus(String paymentStatus);
    
    // Combined queries
    List<MassageRequest> findByCustomerIdAndStatus(String customerId, String status);
    List<MassageRequest> findByCustomerIdAndTherapistId(String customerId, String therapistId);
    
    // Date and time based queries
    List<MassageRequest> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
    List<MassageRequest> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Status-based queries
    List<MassageRequest> findByStatusIn(List<String> statuses);
    
    // Find requests requiring therapist assignment
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.status = 'PENDING' AND mr.therapist IS NULL")
    List<MassageRequest> findUnassignedRequests();
    
    // Find active requests (not completed or cancelled)
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.status IN ('PENDING', 'PREPARATION', 'ACCEPTED', 'IN_PROGRESS')")
    List<MassageRequest> findActiveRequests();
    
    // Find requests ready for completion (in-progress for longer than duration)
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.status = 'IN_PROGRESS' AND mr.startTime <= :cutoffTime")
    List<MassageRequest> findRequestsReadyForCompletion(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // Find requests with panic button activated
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.panicButtonUsed IS NOT NULL AND mr.panicButtonUsed.resolved = false")
    List<MassageRequest> findActivePanicSituations();
    
    // Find requests requiring payment
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.status = 'COMPLETED' AND mr.paymentStatus = 'PENDING'")
    List<MassageRequest> findRequestsRequiringPayment();
    
    // Count queries
    long countByCustomerId(String customerId);
    long countByTherapistId(String therapistId);
    long countByStatus(String status);
    long countByTherapistIdAndStatus(String therapistId, String status);
    
    // Check for scheduling conflicts
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.therapist.id = :therapistId " +
           "AND mr.status IN ('PENDING', 'PREPARATION', 'ACCEPTED', 'IN_PROGRESS') " +
           "AND mr.scheduledTime >= :startTime AND mr.scheduledTime <= :endTime")
    List<MassageRequest> findTherapistConflicts(@Param("therapistId") Long therapistId, 
                                               @Param("startTime") LocalDateTime start, 
                                               @Param("endTime") LocalDateTime end);
    
    // Additional JPA queries
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.therapist.id = :therapistId " +
           "AND mr.status = 'COMPLETED' AND mr.rating IS NOT NULL")
    List<MassageRequest> findRatedRequestsByTherapist(@Param("therapistId") Long therapistId);
    
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.customerId = :customerId " +
           "AND mr.status = 'COMPLETED' AND mr.rating IS NULL")
    List<MassageRequest> findUnratedRequestsByCustomer(@Param("customerId") String customerId);
    
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.status = 'IN_PROGRESS' " +
           "AND mr.startTime IS NOT NULL AND mr.duration IS NOT NULL " +
           "AND (mr.startTime + (mr.duration || ' minutes')::INTERVAL) <= CURRENT_TIMESTAMP")
    List<MassageRequest> findOverdueRequests();
    
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.address.city = :city " +
           "AND mr.status IN ('PENDING', 'PREPARATION', 'ACCEPTED')")
    List<MassageRequest> findAvailableRequestsByCity(@Param("city") String city);
    
    @Query("SELECT mr FROM MassageRequest mr WHERE mr.serviceType = :serviceType " +
           "AND mr.status = 'PENDING'")
    List<MassageRequest> findRequestsByServiceType(@Param("serviceType") String serviceType);
    
    @Query("SELECT DATE(mr.createdAt) as date, COUNT(mr) as requestCount, " +
           "SUM(CASE WHEN mr.status = 'COMPLETED' THEN 1 ELSE 0 END) as completedCount, " +
           "SUM(mr.totalPrice) as totalRevenue " +
           "FROM MassageRequest mr " +
           "WHERE mr.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(mr.createdAt) " +
           "ORDER BY DATE(mr.createdAt) DESC")
    List<Object[]> getDailyRequestStats(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(mr) FROM MassageRequest mr WHERE mr.customerId = :customerId " +
           "AND mr.status = 'COMPLETED' AND mr.rating IS NOT NULL")
    long countRatedRequestsByCustomer(@Param("customerId") String customerId);
    
    @Query("SELECT AVG(mr.rating.score) FROM MassageRequest mr WHERE mr.therapist.id = :therapistId " +
           "AND mr.rating IS NOT NULL AND mr.rating.score IS NOT NULL")
    Double getAverageRatingByTherapist(@Param("therapistId") Long therapistId);
    
    @Query("UPDATE MassageRequest mr SET mr.paymentStatus = :status WHERE mr.id = :requestId")
    int updatePaymentStatus(@Param("requestId") Long requestId, 
                          @Param("status") String status);
    
    @Query("UPDATE MassageRequest mr SET mr.status = :status WHERE mr.id = :requestId")
    int updateRequestStatus(@Param("requestId") Long requestId, 
                          @Param("status") String status);
    List<MassageRequest> findByTherapistIdAndStatus(String therapistId, String status);
}