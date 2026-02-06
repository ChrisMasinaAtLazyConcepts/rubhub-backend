package za.co.rubhub.repositories;

import za.co.rubhub.model.BookingStatus;
import za.co.rubhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    List<User> findByStatus(String status);

    long countByCreatedAtBetween(LocalDateTime start,LocalDateTime end);
    
    Optional<User>  findById(Long id);

    Optional<User> findByPhoneNumber(String phoneNumber);
    
    List<User> findByUserType(String userType);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByIsVerifiedTrue();
    
    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<User> findByUserTypeAndIsActiveTrue(String userType);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType")
    Long countByUserType(@Param("userType") String userType);
    
    @Query("SELECT u FROM User u WHERE u.userType = 'THERAPIST' AND u.isActive = true")
    List<User> findAllActiveTherapists();
    
    @Query("SELECT u FROM User u WHERE u.userType = 'CUSTOMER' AND u.isActive = true")
    List<User> findAllActiveCustomers();
    
    @Query(value = "SELECT DATE(u.created_at) as signupDate, COUNT(*) as count " +
                   "FROM users u " +
                   "WHERE u.created_at >= :startDate AND u.created_at <= :endDate " +
                   "GROUP BY DATE(u.created_at) " +
                   "ORDER BY signupDate", nativeQuery = true)
    List<Object[]> getDailySignupStats(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
}