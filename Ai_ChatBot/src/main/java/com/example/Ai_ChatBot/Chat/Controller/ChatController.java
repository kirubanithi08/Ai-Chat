package com.example.Ai_ChatBot.Chat.Controller;

import com.example.Ai_ChatBot.Chat.Dto.ChatMessageResponse;
import com.example.Ai_ChatBot.Chat.Dto.ChatRequest;
import com.example.Ai_ChatBot.Chat.Dto.ChatResponse;
import com.example.Ai_ChatBot.Chat.Dto.ChatSessionResponse;
import com.example.Ai_ChatBot.Common.ApiResponse;
import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import com.example.Ai_ChatBot.Chat.Service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Chat", description = "AI Chatbot endpoints")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "Send a message and get a full AI response")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request
    ) {
        log.info("Received chat request");
        return ResponseEntity.ok(ApiResponse.success(chatService.chat(request)));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Send a message and get a streaming AI response")
    public SseEmitter streamChat(
            @Valid @RequestBody ChatRequest request
    ) {
        log.info("Received streaming chat request");
        return chatService.streamChat(request);
    }

    @GetMapping("/sessions")
    @Operation(summary = "Get all chat sessions for the current user")
    public ResponseEntity<ApiResponse<List<ChatSessionResponse>>> getSessions() {
        return ResponseEntity.ok(ApiResponse.success(chatService.getUserSessions()));
    }

    @GetMapping("/{sessionId}/messages")
    @Operation(summary = "Get all messages for a specific chat session")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(
            @PathVariable Long sessionId
    ) {
        return ResponseEntity.ok(ApiResponse.success(chatService.getMessages(sessionId)));
    }
}
