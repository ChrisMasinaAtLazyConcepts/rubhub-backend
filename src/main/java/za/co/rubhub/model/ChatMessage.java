package za.co.rubhub.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;
    
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Column(name = "sender_role", length = 20, nullable = false)
    private String senderRole; // customer, therapist, admin, support
    
    @Column(name = "message_type", length = 20)
    @Builder.Default
    private String messageType = "text"; // text, image, file, system
    
    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata; // JSON for additional data like file URLs
    
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (this.sentAt == null) {
            this.sentAt = LocalDateTime.now();
        }
        this.createdAt = LocalDateTime.now();
    }
}