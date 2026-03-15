package com.example.Ai_ChatBot.Ai.Service;

import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AiChatService {

    String generateReply(List<ChatMessage> history);

   Mono<String> generateTitle(String firstMessage); 
    
    Flux<String> streamReply(List<ChatMessage> history);
}



