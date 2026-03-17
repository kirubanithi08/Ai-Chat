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
import com.example.Ai_ChatBot.User.repository.UserRepository;
import com.example.Ai_ChatBot.Common.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AiChatService aiChatService;
    private final ChatPersistenceService chatPersistenceService;
    private final UserRepository userRepository;

  
    private User resolveUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) return null;
        return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
    }

   

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

        if ("New Chat".equals(session.getTitle())) {
            String title = aiChatService.generateTitle(request.getMessage()).block();
            session.setTitle(title);
            chatSessionRepository.save(session);
        }

        return ChatResponse.builder()
                .sessionId(session.getId())
                .userMessage(request.getMessage())
                .aiResponse(aiReply)
                .build();
    }

   

    @Override
    public SseEmitter streamChat(ChatRequest request, Authentication authentication) {
        User user = resolveUser(authentication);
        boolean isGuest = (user == null);

      
        ChatSession session = null;
        List<ChatMessage> history;

        if (!isGuest) {
            session = getOrCreateSession(request, user);

            ChatMessage userMessage = ChatMessage.builder()
                    .session(session)
                    .sender(ChatMessage.Sender.USER)
                    .content(request.getMessage())
                    .createdAt(Instant.now())
                    .build();
            chatMessageRepository.save(userMessage);

            history = chatMessageRepository.findTop10BySessionOrderByCreatedAtDesc(session);
            Collections.reverse(history);
        } else {
           
            ChatMessage guestMessage = ChatMessage.builder()
                    .sender(ChatMessage.Sender.USER)
                    .content(request.getMessage())
                    .createdAt(Instant.now())
                    .build();
            history = new ArrayList<>();
            history.add(guestMessage);
        }

        final ChatSession finalSession = session;
        final boolean guest = isGuest;

        SseEmitter emitter = new SseEmitter(3 * 60 * 1000L);
        StringBuffer fullResponse = new StringBuffer();

        Thread.ofVirtual().start(() -> {

           
            if (!guest) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("session")
                            .data(finalSession.getId().toString()));
                } catch (Exception e) {
                    log.error("Failed to send session event", e);
                    emitter.completeWithError(e);
                    return;
                }
            }

            aiChatService.streamReply(history)
                    .doOnNext(chunk -> {
                        try {
                            fullResponse.append(chunk);
                            emitter.send(SseEmitter.event()
                                    .name("message")
                                    .data(chunk));
                        } catch (Exception e) {
                            log.error("Failed to send chunk", e);
                            emitter.completeWithError(e);
                        }
                    })
                    .doOnComplete(() -> {
                        if (!guest) {
                           
                            chatPersistenceService.saveAiMessageAndTitle(
                                    finalSession, fullResponse.toString(), request.getMessage()
                            ).subscribe(
                                    null,
                                    err -> {
                                        log.error("Failed to save AI message/title", err);
                                        try {
                                            emitter.send(SseEmitter.event()
                                                    .name("error")
                                                    .data("Failed to save message. Please try again."));
                                            emitter.complete();
                                        } catch (Exception e) {
                                            log.error("Emitter error event failed", e);
                                        }
                                    },
                                    () -> {
                                        try {
                                            emitter.send(SseEmitter.event()
                                                    .name("done")
                                                    .data("true"));
                                            emitter.complete();
                                        } catch (Exception e) {
                                            log.error("Emitter complete error", e);
                                        }
                                    }
                            );
                        } else {
                           
                            try {
                                emitter.send(SseEmitter.event()
                                        .name("done")
                                        .data("true"));
                                emitter.complete();
                            } catch (Exception e) {
                                log.error("Emitter complete error (guest)", e);
                            }
                        }
                    })
                    .doOnError(err -> {
                        log.error("Stream error from Gemini", err);
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("AI service error. Please try again."));
                            emitter.complete();
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    })
                    .subscribe();
        });

        return emitter;
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
