package za.co.rubhub.repositories;

import za.co.rubhub.model.Booking;
import za.co.rubhub.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

     Optional<Booking> findById(Long id);
    
     List<Booking>  findCompletedPaidBookingsInDateRange(LocalDateTime start,LocalDateTime end);
    // Find bookings by status and payout processed flag
    List<Booking> findByStatusAndPayoutProcessed(BookingStatus status, Boolean payoutProcessed);
    
    // Find completed bookings for payout within date range
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED' " +
           "AND b.payoutProcessed = false " +
           "AND b.completedAt BETWEEN :startDate AND :endDate")
    List<Booking> findCompletedBookingsForPayout(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    // Find bookings by therapist and status
    List<Booking> findByTherapistIdAndStatus(String therapistId, BookingStatus status);
    
    // Find eligible bookings for payout
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED' " +
           "AND b.payoutProcessed = false " +
           "AND b.paymentStatus = 'PAID'")
    List<Booking> findEligibleBookingsForPayout();
    
    // Find bookings by customer and status
    List<Booking> findByCustomerIdAndStatus(String customerId, BookingStatus status);
    
    // Find bookings by therapist and multiple statuses
    List<Booking> findByTherapistIdAndStatusIn(Long therapistId, List<BookingStatus> statuses);
    
    // Find all bookings by customer
    List<Booking> findByCustomerId(String customerId);
    
    // Find all bookings by therapist
    List<Booking> findByTherapistId(Long therapistId);
    
    // Additional useful queries
    Booking findByRequestId(Long RequestId);
    
    // Find bookings by date range
    List<Booking> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find bookings by scheduled time range
    List<Booking> findByScheduledTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // Find bookings by service
    List<Booking> findByServiceId(Long serviceId);

     List<Booking> findByStatus(BookingStatus status);
    
    // Find bookings by therapist and date
    @Query("SELECT b FROM Booking b WHERE b.therapist.id = :therapistId " +
           "AND DATE(b.date) = DATE(:date)")
    List<Booking> findByTherapistIdAndDate(@Param("therapistId") Long therapistId, 
                                          @Param("date") LocalDateTime date);
    
    // Find upcoming bookings for a therapist
    @Query("SELECT b FROM Booking b WHERE b.therapist.id = :therapistId " +
           "AND b.status IN ('CONFIRMED', 'PENDING') " +
           "AND b.scheduledTime > CURRENT_TIMESTAMP " +
           "ORDER BY b.scheduledTime ASC")
    List<Booking> findUpcomingBookingsByTherapist(@Param("therapistId") Long therapistId);
    
    // Find past bookings for a customer
    @Query("SELECT b FROM Booking b WHERE b.customerId = :customerId " +
           "AND b.status IN ('COMPLETED', 'CANCELLED') " +
           "AND b.scheduledTime < CURRENT_TIMESTAMP " +
           "ORDER BY b.scheduledTime DESC")
    List<Booking> findPastBookingsByCustomer(@Param("customerId") String customerId);
    
    // Find bookings needing payment
    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "AND b.paymentStatus IN ('PENDING', 'PARTIALLY_PAID') " +
           "AND b.scheduledTime > CURRENT_TIMESTAMP")
    List<Booking> findBookingsNeedingPayment();
    
    // Find bookings that can be cancelled (within cancellation window)
    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' " +
           "AND b.scheduledTime > CURRENT_TIMESTAMP " +
           "AND b.scheduledTime <= :cancellationDeadline")
    List<Booking> findBookingsEligibleForCancellation(@Param("cancellationDeadline") LocalDateTime cancellationDeadline);
    
    // Count bookings by status
    long countByStatus(BookingStatus status);

    // Count bookings by countByCustomerId
    long countByCustomerId(String customerId);

    // Count bookings by countByCustomerId
    long countByTherapistId(String customerId);
        
    // Count bookings by therapist and status
    long countByTherapistIdAndStatus(Long therapistId, BookingStatus status);
    
    // Calculate total revenue from completed bookings
    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status = 'COMPLETED'")
    Double getTotalRevenue();
    
    // Calculate therapist earnings
    @Query("SELECT COALESCE(SUM(b.therapistAmount), 0) FROM Booking b " +
           "WHERE b.therapist.id = :therapistId AND b.status = 'COMPLETED'")
    Double getTherapistEarnings(@Param("therapistId") Long therapistId);
    
    // Find bookings with no-shows
    @Query("SELECT b FROM Booking b WHERE b.status = 'NO_SHOW' " +
           "AND b.scheduledTime BETWEEN :startDate AND :endDate")
    List<Booking> findNoShowBookings(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
    
    // Find bookings that need review reminders
    @Query("SELECT b FROM Booking b WHERE b.status = 'COMPLETED' " +
           "AND b.reviewReminderSent = false " +
           "AND b.completedAt <= :reminderDeadline")
    List<Booking> findBookingsNeedingReviewReminder(@Param("reminderDeadline") LocalDateTime reminderDeadline);
    
    // Find overlapping bookings for a therapist
    @Query("SELECT b FROM Booking b WHERE b.therapist.id = :therapistId " +
           "AND b.status NOT IN ('CANCELLED', 'REJECTED') " +
           "AND b.scheduledTime < :endTime " +
           "AND b.endTime > :startTime")
    List<Booking> findOverlappingBookings(@Param("therapistId") Long therapistId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);
    
    // Search bookings by customer name or email
    @Query("SELECT b FROM Booking b WHERE " +
           "(:searchTerm IS NULL OR " +
           "LOWER(b.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.customerEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY b.scheduledTime DESC")
    List<Booking> searchBookings(@Param("searchTerm") String searchTerm);
    
    // Find bookings for dashboard statistics
    @Query("SELECT " +
           "COUNT(CASE WHEN b.status = 'COMPLETED' THEN 1 END) as completed, " +
           "COUNT(CASE WHEN b.status = 'CONFIRMED' THEN 1 END) as confirmed, " +
           "COUNT(CASE WHEN b.status = 'CANCELLED' THEN 1 END) as cancelled, " +
           "COALESCE(SUM(CASE WHEN b.status = 'COMPLETED' THEN b.totalAmount ELSE 0 END), 0) as revenue " +
           "FROM Booking b " +
           "WHERE b.date BETWEEN :startDate AND :endDate")
    Object[] getBookingStatistics(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);
    
    // Update methods
    @Query("UPDATE Booking b SET b.payoutProcessed = true, b.payoutDate = CURRENT_TIMESTAMP " +
           "WHERE b.id IN :bookingIds")
    int markBookingsAsPaid(@Param("bookingIds") List<Long> bookingIds);
    
    @Query("UPDATE Booking b SET b.reviewReminderSent = true WHERE b.id = :bookingId")
    int markReviewReminderSent(@Param("bookingId") Long bookingId);
    
    // Check existence
    boolean existsByTherapistIdAndScheduledTimeBetween(Long therapistId, 
                                                      LocalDateTime startTime, 
                                                      LocalDateTime endTime);

    List<Booking> findActiveBookings();

    List<Booking> findBookingsReadyForPayout();
}