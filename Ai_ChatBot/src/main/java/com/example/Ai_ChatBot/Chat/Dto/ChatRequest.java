package com.example.Ai_ChatBot.Chat.Dto;

import lombok.Data;

@Data
public class ChatRequest {

    private Long sessionId;
    private String message;
}

