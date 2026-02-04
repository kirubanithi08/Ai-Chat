package com.example.Ai_ChatBot.Ai.Service;


import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;

import java.util.List;

public interface AiChatService {

    String generateReply(List<ChatMessage> history);
}


