package com.example.Ai_ChatBot.Chat.Dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatSessionResponse {

    private Long id;
    private String title;
    private Instant createdAt;

}