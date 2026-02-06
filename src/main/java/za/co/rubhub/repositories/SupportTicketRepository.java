package za.co.rubhub.repositories;

import za.co.rubhub.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    
    List<SupportTicket> findByCreatedBy(String createdBy);
    
    List<SupportTicket> findByAssignedTo(String assignedTo);
    
    List<SupportTicket> findByStatus(String status);
    
    List<SupportTicket> findByCategory(String category);
    
    List<SupportTicket> findByPriority(String priority);
    
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    
    @Query("SELECT st FROM SupportTicket st WHERE st.createdAt BETWEEN :startDate AND :endDate")
    List<SupportTicket> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    
    // Additional JPA queries
    List<SupportTicket> findByCustomerEmail(String customerEmail);
    
    List<SupportTicket> findByRelatedBookingId(Long bookingId);
    
    List<SupportTicket> findByRelatedUserId(String userId);
    
    @Query("SELECT st FROM SupportTicket st WHERE st.isEscalated = true")
    List<SupportTicket> findEscalatedTickets();
    
    @Query("SELECT st FROM SupportTicket st WHERE st.slaBreached = true AND st.status NOT IN ('closed', 'resolved')")
    List<SupportTicket> findTicketsWithBreachedSLA();
    
    @Query("SELECT st FROM SupportTicket st WHERE st.followUpRequired = true AND st.followUpDate <= CURRENT_TIMESTAMP")
    List<SupportTicket> findTicketsNeedingFollowUp();
    
    @Query("SELECT st FROM SupportTicket st WHERE st.status IN ('open', 'in-progress') AND st.assignedTo IS NULL")
    List<SupportTicket> findUnassignedActiveTickets();
    
    @Query("SELECT st FROM SupportTicket st WHERE st.createdAt >= :date AND st.status = :status")
    List<SupportTicket> findRecentTicketsByStatus(@Param("date") LocalDateTime date,
                                                 @Param("status") String status);
    
    @Query("SELECT st.category, COUNT(st), " +
           "COUNT(CASE WHEN st.status = 'closed' THEN 1 END) as closedCount, " +
           "AVG(st.resolutionTimeMinutes) as avgResolutionTime " +
           "FROM SupportTicket st " +
           "WHERE st.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY st.category")
    List<Object[]> getTicketStatsByCategory(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT st.assignedTo, COUNT(st), " +
           "AVG(st.firstResponseTimeMinutes) as avgFirstResponse, " +
           "AVG(st.resolutionTimeMinutes) as avgResolutionTime " +
           "FROM SupportTicket st " +
           "WHERE st.status = 'closed' AND st.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY st.assignedTo")
    List<Object[]> getAgentPerformanceStats(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(st) FROM SupportTicket st WHERE st.createdBy = :createdBy AND st.status = 'open'")
    long countOpenTicketsByUser(@Param("createdBy") String createdBy);
    
    @Query("SELECT st FROM SupportTicket st WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(st.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(st.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(st.customerEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY st.createdAt DESC")
    List<SupportTicket> searchTickets(@Param("searchTerm") String searchTerm);
    
    @Query("UPDATE SupportTicket st SET st.status = :status, st.closedAt = CURRENT_TIMESTAMP " +
           "WHERE st.id = :ticketId")
    int closeTicket(@Param("ticketId") Long ticketId, @Param("status") String status);
    
    @Query("UPDATE SupportTicket st SET st.assignedTo = :assignedTo WHERE st.id = :ticketId")
    int assignTicket(@Param("ticketId") Long ticketId, @Param("assignedTo") String assignedTo);
    
    @Query("UPDATE SupportTicket st SET st.satisfactionScore = :score, st.satisfactionFeedback = :feedback " +
           "WHERE st.id = :ticketId")
    int updateSatisfaction(@Param("ticketId") Long ticketId,
                          @Param("score") Integer score,
                          @Param("feedback") String feedback);
    
    long countByStatus(String status);
    
    long countByCategoryAndStatus(String category, String status);
    
    boolean existsByTicketNumber(String ticketNumber);
}