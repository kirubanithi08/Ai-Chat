package com.example.Ai_ChatBot.Chat.Dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {

    private Long sessionId;
    private String userMessage;
    private String aiResponse;
}

