package za.co.rubhub.repositories;

import za.co.rubhub.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    List<ChatMessage> findByChatIdOrderBySentAtAsc(Long chatId);
    
    List<ChatMessage> findByChatIdAndSenderId(Long chatId, Long senderId);
    
    List<ChatMessage> findByChatIdAndIsReadFalse(Long chatId);
    
    List<ChatMessage> findByChatIdAndSentAtAfter(Long chatId, LocalDateTime sentAt);
    
    List<ChatMessage> findByChatIdAndMessageType(Long chatId, String messageType);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chat.id = :chatId AND m.sentAt = (SELECT MAX(m2.sentAt) FROM ChatMessage m2 WHERE m2.chat.id = :chatId)")
    Optional<ChatMessage> findLastMessageByChatId(@Param("chatId") Long chatId);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chat.id = :chatId AND m.isRead = false")
    Long countUnreadMessagesByChatId(@Param("chatId") Long chatId);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chat.id = :chatId AND m.senderId = :senderId AND m.isRead = false")
    Long countUnreadMessagesFromSender(@Param("chatId") Long chatId, @Param("senderId") Long senderId);
    
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true, m.readAt = :readAt WHERE m.chat.id = :chatId AND m.senderId != :userId AND m.isRead = false")
    void markMessagesAsRead(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);
    
    @Query(value = "SELECT DATE(m.sent_at) as messageDate, COUNT(*) as messageCount " +
                   "FROM chat_messages m " +
                   "WHERE m.sent_at BETWEEN :startDate AND :endDate " +
                   "GROUP BY DATE(m.sent_at) " +
                   "ORDER BY messageDate", nativeQuery = true)
    List<Object[]> getMessageStatsByDate(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chat.requestId = :requestId ORDER BY m.sentAt ASC")
    List<ChatMessage> findByRequestId(@Param("requestId") String requestId);
    
    long countByChatId(Long chatId);
}