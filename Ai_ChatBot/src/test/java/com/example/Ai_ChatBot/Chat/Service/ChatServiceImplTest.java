package com.example.Ai_ChatBot.Chat.Service;

import com.example.Ai_ChatBot.Ai.Service.AiChatService;
import com.example.Ai_ChatBot.Chat.Dto.ChatRequest;
import com.example.Ai_ChatBot.Chat.Dto.ChatResponse;
import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import com.example.Ai_ChatBot.Chat.Repository.ChatMessageRepository;
import com.example.Ai_ChatBot.Chat.Repository.ChatSessionRepository;
import com.example.Ai_ChatBot.Common.SecurityUtils;
import com.example.Ai_ChatBot.User.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChatSessionRepository chatSessionRepository;
    @Mock
    private AiChatService aiChatService;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User user;
    private ChatSession session;
    private ChatRequest chatRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@example.com").build();
        session = ChatSession.builder().id(1L).title("New Chat").user(user).build();
        chatRequest = new ChatRequest(null, "Hello AI");
    }

    @Test
    void chat_Success_NewSession() {
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(user);
            
            when(chatSessionRepository.save(any(ChatSession.class))).thenReturn(session);
            when(chatMessageRepository.findTop10BySessionOrderByCreatedAtDesc(any())).thenReturn(new ArrayList<>());
            when(aiChatService.generateReply(anyList())).thenReturn("Hi there!");
            when(aiChatService.generateTitle(anyString())).thenReturn("Greeting");

            ChatResponse response = chatService.chat(chatRequest);

            assertNotNull(response);
            assertEquals("Hi there!", response.getAiResponse());
            verify(chatSessionRepository, atLeastOnce()).save(any(ChatSession.class));
            verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
        }
    }

    @Test
    void chat_Success_ExistingSession() {
        chatRequest.setSessionId(1L);
        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentUser).thenReturn(user);
            
            when(chatSessionRepository.findById(1L)).thenReturn(Optional.of(session));
            when(chatMessageRepository.findTop10BySessionOrderByCreatedAtDesc(any())).thenReturn(new ArrayList<>());
            when(aiChatService.generateReply(anyList())).thenReturn("How can I help?");

            ChatResponse response = chatService.chat(chatRequest);

            assertNotNull(response);
            assertEquals("How can I help?", response.getAiResponse());
            verify(chatMessageRepository, times(2)).save(any(ChatMessage.class));
        }
    }
}
