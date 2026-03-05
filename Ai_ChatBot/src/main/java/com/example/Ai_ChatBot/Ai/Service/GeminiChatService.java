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
    private final PromptBuilder promptBuilder;

    @Override
    public String generateReply(List<ChatMessage> history) {

        String prompt = promptBuilder.buildPrompt(history);

        GeminiRequest request = GeminiRequest.builder()
                .contents(List.of(
                        new GeminiRequest.Content(
                                List.of(new GeminiRequest.Part(prompt))
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