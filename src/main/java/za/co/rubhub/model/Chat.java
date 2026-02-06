package za.co.rubhub.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "messages")
@EqualsAndHashCode(exclude = "messages")
public class Chat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ElementCollection
    @CollectionTable(name = "chat_participants", joinColumns = @JoinColumn(name = "chat_id"))
    private List<Participant> participants = new ArrayList<>();
    
    @Column(name = "request_id", length = 50)
    private String requestId;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Embedded
    private LastMessage lastMessage;
    
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Participant {
        @Column(name = "user_id", nullable = false)
        private Long userId;
        
        @Column(name = "role", length = 20, nullable = false)
        private String role; // customer, therapist, admin, support
    }

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LastMessage {
        @Column(name = "last_message_content", columnDefinition = "TEXT")
        private String content;
        
        @Column(name = "last_message_sent_at")
        private LocalDateTime sentAt;
        
        @Column(name = "last_message_sent_by")
        private Long sentBy;
    }
}