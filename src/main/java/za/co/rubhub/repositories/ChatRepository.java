package za.co.rubhub.repositories;

import za.co.rubhub.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByRequestId(String requestId);
    
    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.userId = :userId AND c.isActive = true")
    List<Chat> findByParticipantUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.userId = :userId AND p.role = :role AND c.isActive = true")
    List<Chat> findByParticipantUserIdAndRole(@Param("userId") Long userId, @Param("role") String role);
    
    @Query("SELECT c FROM Chat c WHERE :userId IN (SELECT p.userId FROM c.participants p) AND :otherUserId IN (SELECT p.userId FROM c.participants p)")
    Optional<Chat> findDirectChatBetweenUsers(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId);
    
    @Query("SELECT c FROM Chat c WHERE c.requestId = :requestId AND c.isActive = true")
    Optional<Chat> findActiveByRequestId(@Param("requestId") String requestId);
    
    @Query("SELECT c FROM Chat c WHERE c.lastMessage.sentAt IS NOT NULL AND c.lastMessage.sentAt < :cutoffTime")
    List<Chat> findInactiveChats(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.isActive = true")
    Long countActiveChats();
    
    @Query("SELECT c FROM Chat c WHERE c.updatedAt < :cutoffTime AND c.isActive = true")
    List<Chat> findStaleChats(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    boolean existsByRequestId(String requestId);

    List<Chat> findActiveChatsByUserId(String userId);
}