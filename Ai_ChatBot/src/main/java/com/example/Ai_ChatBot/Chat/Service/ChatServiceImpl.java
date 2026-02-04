package com.example.Ai_ChatBot.Chat.Service;

import com.example.Ai_ChatBot.Ai.Service.AiChatService;
import com.example.Ai_ChatBot.Chat.Dto.ChatRequest;
import com.example.Ai_ChatBot.Chat.Dto.ChatResponse;
import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import com.example.Ai_ChatBot.Chat.Repository.ChatMessageRepository;
import com.example.Ai_ChatBot.User.entity.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final AiChatService aiChatService;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public ChatResponse chat(ChatRequest request) {

        User user = getAuthenticatedUser();

        ChatSession session = getOrCreateSession(request, user);


        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .sender(ChatMessage.Sender.USER)
                .content(request.getMessage())
                .createdAt(Instant.now())
                .build();

        chatMessageRepository.save(userMessage);


        List<ChatMessage> history =
                chatMessageRepository.findBySessionOrderByCreatedAtAsc(session);


        String aiReply = aiChatService.generateReply(history);


        ChatMessage aiMessage = ChatMessage.builder()
                .session(session)
                .sender(ChatMessage.Sender.AI)
                .content(aiReply)
                .createdAt(Instant.now())
                .build();

        chatMessageRepository.save(aiMessage);

        return ChatResponse.builder()
                .sessionId(session.getId())
                .userMessage(request.getMessage())
                .aiResponse(aiReply)
                .build();
    }



    private ChatSession getOrCreateSession(
            ChatRequest request,
            User user
    ) {
        if (request.getSessionId() != null) {
            return entityManager.find(ChatSession.class, request.getSessionId());
        }

        ChatSession session = ChatSession.builder()
                .title("New Chat")
                .user(user)
                .createdAt(Instant.now())
                .build();

        entityManager.persist(session);
        return session;
    }

    private User getAuthenticatedUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}

