package com.example.Ai_ChatBot.Chat.Repository;

import com.example.Ai_ChatBot.Chat.Entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findByUserIdOrderByCreatedAtDesc(Long userId);

}