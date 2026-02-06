package za.co.rubhub.repositories;

import za.co.rubhub.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    
    Optional<Account>  findById(Long id);
    Optional<Account> findByUserIdAndAccountType(String userId, String accountType);
    
    List<Account> findByAccountType(String accountType);
    
    Optional<Account> findByAccountNumber(String accountNumber);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.isActive = true")
    List<Account> findActiveAccountsByUserId(@Param("userId") String userId);
    
    Optional<Account> findByUserIdAndAccountTypeAndIsActive(String userId, String accountType, Boolean isActive);
    
    // Additional JPA-specific queries
    
    List<Account> findByUserId(String userId);
    
    List<Account> findByIsActiveTrue();
    
    List<Account> findByIsActiveFalse();
    
    List<Account> findByUserIdAndIsActive(String userId, Boolean isActive);
    
    @Query("SELECT a FROM Account a WHERE a.balance >= :minBalance AND a.isActive = true")
    List<Account> findActiveAccountsWithMinimumBalance(@Param("minBalance") Double minBalance);
    
    @Query("SELECT a FROM Account a WHERE a.balance < 0 AND a.isActive = true")
    List<Account> findActiveAccountsWithNegativeBalance();
    
    @Query("SELECT a FROM Account a WHERE a.bankName = :bankName AND a.isActive = true")
    List<Account> findActiveAccountsByBankName(@Param("bankName") String bankName);
    
    @Query("SELECT a FROM Account a WHERE a.branchCode = :branchCode")
    List<Account> findByBranchCode(@Param("branchCode") String branchCode);
    
    @Query("SELECT a FROM Account a WHERE a.userId = :userId AND a.accountType IN :accountTypes")
    List<Account> findByUserIdAndAccountTypeIn(@Param("userId") String userId, 
                                               @Param("accountTypes") List<String> accountTypes);
    
    @Query("SELECT COUNT(a) FROM Account a WHERE a.userId = :userId AND a.isActive = true")
    Long countActiveAccountsByUserId(@Param("userId") String userId);
    
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.userId = :userId AND a.isActive = true")
    Double getTotalBalanceByUserId(@Param("userId") String userId);
    
    @Query("SELECT a.accountType, COUNT(a), SUM(a.balance), AVG(a.balance) " +
           "FROM Account a WHERE a.isActive = true " +
           "GROUP BY a.accountType")
    List<Object[]> getAccountTypeStatistics();
    
    @Query("SELECT a FROM Account a WHERE a.isActive = true AND a.verificationStatus != 'VERIFIED'")
    List<Account> findActiveUnverifiedAccounts();
    
    @Query("UPDATE Account a SET a.balance = a.balance + :amount WHERE a.id = :accountId")
    int addToBalance(@Param("accountId") UUID accountId, @Param("amount") Double amount);
    
    @Query("UPDATE Account a SET a.balance = a.balance - :amount WHERE a.id = :accountId AND a.balance >= :amount")
    int subtractFromBalance(@Param("accountId") UUID accountId, @Param("amount") Double amount);
    
    @Query("UPDATE Account a SET a.isActive = false, a.deactivatedAt = CURRENT_TIMESTAMP " +
           "WHERE a.id = :accountId AND a.balance = 0")
    int deactivateZeroBalanceAccount(@Param("accountId") UUID accountId);
    
    boolean existsByAccountNumber(String accountNumber);
    
    boolean existsByUserIdAndAccountType(String userId, String accountType);
    
    boolean existsByUserIdAndAccountNumber(String userId, String accountNumber);
}