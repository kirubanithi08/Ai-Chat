package com.example.Ai_ChatBot.Ai.client;

import com.example.Ai_ChatBot.Ai.dto.GeminiRequest;
import com.example.Ai_ChatBot.Ai.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient geminiWebClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    public GeminiResponse generate(GeminiRequest request) {

        return geminiWebClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/models/gemini-3-flash-preview:generateContent")
                                .queryParam("key", apiKey)
                                .build()
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(500))
                                .filter(ex -> ex instanceof WebClientRequestException)
                )
                .block();
    }
}
