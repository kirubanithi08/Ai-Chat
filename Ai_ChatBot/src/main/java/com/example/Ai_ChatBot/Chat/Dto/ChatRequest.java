package com.example.Ai_ChatBot.Chat.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequest {

    @Schema(description = "Chat session identifier", example = "1001")
    private Long sessionId;
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    @Schema(description = "User message", example = "Explain Spring Boot")
    private String message;
}

