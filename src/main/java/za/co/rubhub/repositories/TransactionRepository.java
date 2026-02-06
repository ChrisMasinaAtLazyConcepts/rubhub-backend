package za.co.rubhub.repositories;

import za.co.rubhub.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Transaction> findByReferenceAndTransactionType(String reference, String transactionType);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccountId = :fromAccountId AND t.status = 'SUCCESS'")
    List<Transaction> findSuccessfulTransactionsFromAccount(@Param("fromAccountId") String fromAccountId);
    
    @Query("SELECT t FROM Transaction t WHERE t.toAccountId = :toAccountId AND t.status = 'SUCCESS'")
    List<Transaction> findSuccessfulTransactionsToAccount(@Param("toAccountId") String toAccountId);
    
    List<Transaction> findByStatus(String status);
    
    // Additional JPA queries
    List<Transaction> findByTransactionType(String transactionType);
    
    List<Transaction> findByUserId(String userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.status = 'SUCCESS' " +
           "ORDER BY t.transactionDate DESC")
    List<Transaction> findSuccessfulTransactionsByUser(@Param("userId") String userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.fromAccountId = :accountId OR t.toAccountId = :accountId")
    List<Transaction> findByAccountId(@Param("accountId") String accountId);
    
    @Query("SELECT t FROM Transaction t WHERE (t.fromAccountId = :accountId OR t.toAccountId = :accountId) " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") String accountId,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.toAccountId = :accountId AND t.status = 'SUCCESS'")
    BigDecimal getTotalCreditsToAccount(@Param("accountId") String accountId);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.fromAccountId = :accountId AND t.status = 'SUCCESS'")
    BigDecimal getTotalDebitsFromAccount(@Param("accountId") String accountId);
    
    @Query("SELECT t.transactionType, COUNT(t), SUM(t.amount), AVG(t.amount) " +
           "FROM Transaction t WHERE t.status = 'SUCCESS' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.transactionType")
    List<Object[]> getTransactionTypeStats(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT DATE(t.transactionDate) as transactionDate, COUNT(t) as transactionCount, " +
           "SUM(t.amount) as totalAmount, COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) as successCount " +
           "FROM Transaction t " +
           "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(t.transactionDate) " +
           "ORDER BY DATE(t.transactionDate) DESC")
    List<Object[]> getDailyTransactionStats(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.reference LIKE CONCAT(:prefix, '%')")
    List<Transaction> findByReferencePrefix(@Param("prefix") String prefix);
    
    @Query("SELECT t FROM Transaction t WHERE t.status IN ('FAILED', 'CANCELLED') " +
           "AND t.transactionDate >= :since")
    List<Transaction> findRecentFailedTransactions(@Param("since") LocalDateTime since);
    
    @Query("SELECT t FROM Transaction t WHERE t.amount >= :minAmount AND t.status = 'SUCCESS'")
    List<Transaction> findLargeTransactions(@Param("minAmount") BigDecimal minAmount);
    
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.status = 'SUCCESS'")
    long countSuccessfulTransactionsByUser(@Param("userId") String userId);
    
    @Query("UPDATE Transaction t SET t.status = :status WHERE t.id = :transactionId")
    int updateTransactionStatus(@Param("transactionId") Long transactionId,
                               @Param("status") String status);
    
    @Query("UPDATE Transaction t SET t.status = 'FAILED', t.failureReason = :reason " +
           "WHERE t.id = :transactionId")
    int markTransactionAsFailed(@Param("transactionId") Long transactionId,
                               @Param("reason") String reason);
    
    boolean existsByReference(String reference);
    
    boolean existsByTransactionId(String transactionId);
}