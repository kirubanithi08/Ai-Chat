package com.example.Ai_ChatBot.Chat.Repository;

import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop10BySessionOrderByCreatedAtDesc(
            ChatSession session
    );

    List<ChatMessage> findBySessionOrderByCreatedAtAsc(
            ChatSession session
    );


}


