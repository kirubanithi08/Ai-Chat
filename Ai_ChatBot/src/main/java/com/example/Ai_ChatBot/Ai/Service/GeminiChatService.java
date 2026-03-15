package com.example.Ai_ChatBot.Ai.Service;

import com.example.Ai_ChatBot.Ai.client.GeminiClient;
import com.example.Ai_ChatBot.Ai.dto.GeminiRequest;
import com.example.Ai_ChatBot.Ai.dto.GeminiResponse;
import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiChatService implements AiChatService {

    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;

  @Override
public String generateReply(List<ChatMessage> history) {
    GeminiRequest request = promptBuilder.buildRequest(history);
    return extractText(geminiClient.generate(request).block());
}

@Override
public Mono<String> generateTitle(String firstMessage) { 
    String prompt = """
            Generate a short chat title (max 6 words) for this user message.
            Only return the title, no punctuation.
            Message: %s
            """.formatted(firstMessage);
    GeminiRequest request = GeminiRequest.builder()
            .contents(List.of(new GeminiRequest.Content(
                    "user", List.of(new GeminiRequest.Part(prompt))
            )))
            .build();
    return geminiClient.generate(request)
            .map(response -> extractText(response).trim()); 
}

    @Override
    public Flux<String> streamReply(List<ChatMessage> history) {
        GeminiRequest request = promptBuilder.buildRequest(history);

        return geminiClient.streamGenerate(request)
                .map(this::extractText)
                .filter(text -> !text.isBlank())
                .doOnError(e -> log.error("Gemini stream error", e));
    }

    private String extractText(GeminiResponse response) {
        try {
            GeminiResponse.Candidate candidate = response.getCandidates().get(0);

            if ("SAFETY".equals(candidate.getFinishReason())) {
                log.warn("Gemini blocked response due to safety filters");
                return "I'm sorry, I can't respond to that.";
            }

            String text = candidate.getContent().getParts().get(0).getText();
            return text != null ? text : "";

        } catch (Exception e) {
            log.warn("Failed to extract text from Gemini response: {}", e.getMessage());
            return "";
        }
    }
}
