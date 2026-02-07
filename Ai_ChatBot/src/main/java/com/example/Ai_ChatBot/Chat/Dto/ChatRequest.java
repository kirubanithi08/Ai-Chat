package com.example.Ai_ChatBot.Chat.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatRequest {

    private Long sessionId;
    @NotBlank
    @Size(max = 2000)
    private String message;
}

