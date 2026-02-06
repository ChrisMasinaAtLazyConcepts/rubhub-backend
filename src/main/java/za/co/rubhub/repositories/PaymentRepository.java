package za.co.rubhub.repositories;

import za.co.rubhub.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Optional<Payment> findByProviderTransactionId(String providerTransactionId);
    
    List<Payment> findByUserId(String userId);
    
    List<Payment> findByBookingId(String bookingId);
    
    List<Payment> findByTherapistId(Long therapistId);
    
    List<Payment> findByStatus(Payment.PaymentStatus status);
    
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    boolean existsByTransactionId(String transactionId);
    
    // Additional JPA queries
    List<Payment> findByCustomerId(String customerId);
    
    @Query("SELECT p FROM Payment p WHERE p.customerId = :customerId AND p.status = 'COMPLETED'")
    List<Payment> findSuccessfulPaymentsByCustomer(@Param("customerId") String customerId);
    
    @Query("SELECT p FROM Payment p WHERE p.therapist.id = :therapistId AND p.status = 'COMPLETED' AND p.payoutDate IS NULL")
    List<Payment> findUnpaidPaymentsByTherapist(@Param("therapistId") Long therapistId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.therapist.id = :therapistId AND p.status = 'COMPLETED'")
    BigDecimal getTotalEarningsByTherapist(@Param("therapistId") Long therapistId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueBetween(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY p.paymentMethod")
    List<Object[]> getPaymentMethodStats(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT p.currency, COUNT(p), SUM(p.amount), AVG(p.amount) FROM Payment p " +
           "WHERE p.status = 'COMPLETED' GROUP BY p.currency")
    List<Object[]> getCurrencyStats();
    
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.paymentDate >= :since")
    List<Payment> findRecentFailedPayments(@Param("since") LocalDateTime since);
    
    @Query("SELECT p FROM Payment p WHERE p.status = 'REFUNDED' OR p.status = 'PARTIALLY_REFUNDED'")
    List<Payment> findRefundedPayments();
    
    @Query("SELECT DATE(p.paymentDate) as paymentDate, COUNT(p) as paymentCount, " +
           "SUM(p.amount) as totalAmount, SUM(p.platformFee) as totalFees " +
           "FROM Payment p WHERE p.status = 'COMPLETED' " +
           "AND p.paymentDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(p.paymentDate) " +
           "ORDER BY DATE(p.paymentDate) DESC")
    List<Object[]> getDailyPaymentStats(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status AND p.paymentDate >= :date")
    long countByStatusAndPaymentDateAfter(@Param("status") Payment.PaymentStatus status,
                                         @Param("date") LocalDateTime date);
    
    @Query("UPDATE Payment p SET p.status = :status, p.processedDate = CURRENT_TIMESTAMP " +
           "WHERE p.id = :paymentId")
    int updatePaymentStatus(@Param("paymentId") Long paymentId,
                          @Param("status") Payment.PaymentStatus status);
    
    @Query("UPDATE Payment p SET p.payoutDate = CURRENT_TIMESTAMP WHERE p.id IN :paymentIds")
    int markPaymentsAsPaid(@Param("paymentIds") List<Long> paymentIds);
    
    boolean existsByProviderTransactionId(String providerTransactionId);
    
    boolean existsByBookingId(String bookingId);
}