package za.co.rubhub.service;
import za.co.rubhub.model.Chat;
import za.co.rubhub.model.ChatMessage;
import za.co.rubhub.repositories.ChatMessageRepository;
import za.co.rubhub.repositories.ChatRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Service
@NoArgsConstructor
public class ChatService {
    private  ChatRepository chatRepository;
    
    private  ChatMessageRepository messageRepository;
    

    public Chat createChat(Chat chat) {
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());
        return chatRepository.save(chat);
    }

    public List<Chat> getUserChats(String userId) {
        return chatRepository.findActiveChatsByUserId(userId);
    }

    public Optional<Chat> getChatById(Long id) {
        return chatRepository.findById(id);
    }

    public ChatMessage sendMessage(ChatMessage message) {
        message.setCreatedAt(LocalDateTime.now());
        
        // Update last message in chat
        chatRepository.findById(message.getId()).ifPresent(chat -> {
            Chat.LastMessage lastMessage = new Chat.LastMessage();
            lastMessage.setContent(message.getContent());
            lastMessage.setSentAt(message.getCreatedAt());
            lastMessage.setSentBy(message.getSenderId());
            chat.setLastMessage(lastMessage);
            chat.setUpdatedAt(LocalDateTime.now());
            chatRepository.save(chat);
        });
        
        return messageRepository.save(message);
    }

    public List<ChatMessage> getChatMessages(String chatId) {
        return messageRepository.findAll();
    }

    public Chat deactivateChat(Long chatId) {
        return chatRepository.findById(chatId).map(chat -> {
            chat.setIsActive(false);
            chat.setUpdatedAt(LocalDateTime.now());
            return chatRepository.save(chat);
        }).orElseThrow(() -> new RuntimeException("Chat not found"));
    }
}
