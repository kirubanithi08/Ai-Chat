package com.example.Ai_ChatBot.Chat.Service;

import com.example.Ai_ChatBot.Ai.Service.AiChatService;
import com.example.Ai_ChatBot.Chat.Dto.ChatMessageResponse;
import com.example.Ai_ChatBot.Chat.Dto.ChatRequest;
import com.example.Ai_ChatBot.Chat.Dto.ChatResponse;
import com.example.Ai_ChatBot.Chat.Dto.ChatSessionResponse;
import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import com.example.Ai_ChatBot.Chat.Repository.ChatMessageRepository;
import com.example.Ai_ChatBot.Chat.Repository.ChatSessionRepository;
import com.example.Ai_ChatBot.User.entity.User;
import com.example.Ai_ChatBot.Common.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiChatService aiChatService;

    @Override
    @Transactional
    public ChatResponse chat(ChatRequest request) {

        User user = SecurityUtils.getCurrentUser();

        ChatSession session = getOrCreateSession(request, user);


        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .sender(ChatMessage.Sender.USER)
                .content(request.getMessage())
                .createdAt(Instant.now())
                .build();

        chatMessageRepository.save(userMessage);


        List<ChatMessage> history =
                chatMessageRepository.findTop10BySessionOrderByCreatedAtDesc(session);

        Collections.reverse(history);


        String aiReply = aiChatService.generateReply(history);


        ChatMessage aiMessage = ChatMessage.builder()
                .session(session)
                .sender(ChatMessage.Sender.AI)
                .content(aiReply)
                .createdAt(Instant.now())
                .build();

        chatMessageRepository.save(aiMessage);

        // Generate session title for new chats
        if ("New Chat".equals(session.getTitle())) {
            String title = aiChatService.generateTitle(request.getMessage());
            session.setTitle(title);
            chatSessionRepository.save(session);
        }

        return ChatResponse.builder()
                .sessionId(session.getId())
                .userMessage(request.getMessage())
                .aiResponse(aiReply)
                .build();
    }

    private ChatSession getOrCreateSession(ChatRequest request, User user) {

        if (request.getSessionId() != null) {

            ChatSession session = chatSessionRepository
                    .findById(request.getSessionId())
                    .orElseThrow(() -> new IllegalStateException("Chat session not found"));

            if (!session.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("Access denied to chat session");
            }

            return session;
        }

        ChatSession session = ChatSession.builder()
                .title("New Chat")
                .user(user)
                .createdAt(Instant.now())
                .build();

        return chatSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getUserSessions() {

        User user = SecurityUtils.getCurrentUser();

        return chatSessionRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(session -> ChatSessionResponse.builder()
                        .id(session.getId())
                        .title(session.getTitle())
                        .createdAt(session.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long sessionId) {

        User user = SecurityUtils.getCurrentUser();

        ChatSession session = chatSessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new IllegalStateException("Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("Access denied");
        }

        return chatMessageRepository
                .findBySessionOrderByCreatedAtAsc(session)
                .stream()
                .map(message -> ChatMessageResponse.builder()
                        .sender(message.getSender())
                        .content(message.getContent())
                        .createdAt(message.getCreatedAt())
                        .build())
                .toList();
    }
}