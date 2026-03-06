package com.example.Ai_ChatBot.Chat.Dto;

import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ChatMessageResponse {

    private ChatMessage.Sender sender;
    private String content;
    private Instant createdAt;

}