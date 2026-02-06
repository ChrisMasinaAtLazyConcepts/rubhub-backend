package za.co.rubhub.controller;

import za.co.rubhub.model.Chat;
import za.co.rubhub.model.ChatMessage;
import za.co.rubhub.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody Chat chat) {
        return ResponseEntity.ok(chatService.createChat(chat));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Chat>> getUserChats(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getUserChats(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable Long id) {
        return chatService.getChatById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ChatMessage> sendMessage(@PathVariable String chatId, @RequestBody ChatMessage message) {
        return ResponseEntity.ok(chatService.sendMessage(message));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable String chatId) {
        return ResponseEntity.ok(chatService.getChatMessages(chatId));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Chat> deactivateChat(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.deactivateChat(id));
    }
}