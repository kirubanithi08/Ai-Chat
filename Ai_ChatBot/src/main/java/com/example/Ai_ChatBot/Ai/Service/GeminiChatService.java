package com.example.Ai_ChatBot.Ai.Service;

import com.example.Ai_ChatBot.Ai.client.GeminiClient;
import com.example.Ai_ChatBot.Ai.dto.GeminiRequest;
import com.example.Ai_ChatBot.Ai.dto.GeminiResponse;
import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiChatService implements AiChatService {

    private final GeminiClient geminiClient;

    @Override
    public String generateReply(List<ChatMessage> history) {

        GeminiRequest request = GeminiRequest.builder()
                .contents(List.of(
                        new GeminiRequest.Content(
                                history.stream()
                                        .map(m ->
                                                new GeminiRequest.Part(
                                                        m.getSender() + ": " + m.getContent()
                                                )
                                        )
                                        .toList()
                        )
                ))
                .build();

        GeminiResponse response = geminiClient.generate(request);

        return response
                .getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }
}
