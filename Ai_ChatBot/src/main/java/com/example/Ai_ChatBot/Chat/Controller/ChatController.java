package com.example.Ai_ChatBot.Chat.Controller;

import com.example.Ai_ChatBot.Chat.Dto.ChatMessageResponse;
import com.example.Ai_ChatBot.Chat.Dto.ChatRequest;
import com.example.Ai_ChatBot.Chat.Dto.ChatResponse;
import com.example.Ai_ChatBot.Chat.Dto.ChatSessionResponse;
import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import com.example.Ai_ChatBot.Chat.Service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @Valid @RequestBody ChatRequest request
    ) {
        return ResponseEntity.ok(chatService.chat(request));
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponse>> getSessions() {
        return ResponseEntity.ok(chatService.getUserSessions());
    }

    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(chatService.getMessages(sessionId));
    }
}
