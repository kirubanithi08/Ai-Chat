package com.example.Ai_ChatBot.Chat.Service;


import com.example.Ai_ChatBot.Chat.Dto.ChatRequest;
import com.example.Ai_ChatBot.Chat.Dto.ChatResponse;

public interface ChatService {

    ChatResponse chat(ChatRequest request);
}
