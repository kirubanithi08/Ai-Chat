package com.example.Ai_ChatBot.Chat.Service;

import com.example.Ai_ChatBot.Ai.Service.AiChatService;
import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import com.example.Ai_ChatBot.Chat.Repository.ChatMessageRepository;
import com.example.Ai_ChatBot.Chat.Repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatPersistenceService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiChatService aiChatService;

   
    public Mono<Void> saveAiMessageAndTitle(ChatSession session,
                                             String aiReply,
                                             String userMessage) {
        return Mono.fromCallable(() -> {
                   
                    ChatMessage aiMessage = ChatMessage.builder()
                            .session(session)
                            .sender(ChatMessage.Sender.AI)
                            .content(aiReply)
                            .createdAt(Instant.now())
                            .build();
                    chatMessageRepository.save(aiMessage);
                    log.debug("AI message saved for session {}", session.getId());
                    return session;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(s -> {
                   
                    if (!"New Chat".equals(s.getTitle())) {
                        log.debug("Session {} already has title, skipping", s.getId());
                        return Mono.empty();
                    }
                   
                    return aiChatService.generateTitle(userMessage)
                            .flatMap(title -> Mono.fromCallable(() -> {
                                s.setTitle(title);
                                chatSessionRepository.save(s);
                                log.debug("Title '{}' saved for session {}", title, s.getId());
                                return s;
                            }).subscribeOn(Schedulers.boundedElastic()));
                })
               
                .onErrorResume(ex -> {
                    log.error("Failed to save AI message or title for session {}: {}",
                            session.getId(), ex.getMessage());
                    return Mono.empty();
                })
                .then();
    }
}
