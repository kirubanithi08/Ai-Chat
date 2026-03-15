package com.example.Ai_ChatBot.Chat.Service;

import com.example.Ai_ChatBot.Ai.Service.AiChatService;
import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import com.example.Ai_ChatBot.Chat.Repository.ChatMessageRepository;
import com.example.Ai_ChatBot.Chat.Repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChatPersistenceService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiChatService aiChatService;

    @Transactional
    public void saveAiMessageAndTitle(ChatSession session, String aiReply, String userMessage) {
        ChatMessage aiMessage = ChatMessage.builder()
                .session(session)
                .sender(ChatMessage.Sender.AI)
                .content(aiReply)
                .createdAt(Instant.now())
                .build();
        chatMessageRepository.save(aiMessage);

        if ("New Chat".equals(session.getTitle())) {
            String title = aiChatService.generateTitle(userMessage);
            session.setTitle(title);
            chatSessionRepository.save(session);
        }
    }
}