package com.example.Ai_ChatBot.Service;

import org.springframework.stereotype.Service;

@Service
public interface ChatService {
    String chat(String message);
}
