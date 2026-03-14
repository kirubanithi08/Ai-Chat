package com.example.Ai_ChatBot.Chat.Service;

import com.example.Ai_ChatBot.Chat.Dto.ChatMessageResponse;
import com.example.Ai_ChatBot.Chat.Dto.ChatRequest;
import com.example.Ai_ChatBot.Chat.Dto.ChatResponse;
import com.example.Ai_ChatBot.Chat.Dto.ChatSessionResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {

    ChatResponse chat(ChatRequest request);

    List<ChatSessionResponse> getUserSessions();

    List<ChatMessageResponse> getMessages(Long sessionId);

    SseEmitter streamChat(ChatRequest request);
}
