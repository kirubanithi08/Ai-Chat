package com.example.Ai_ChatBot.Ai.client;

import com.example.Ai_ChatBot.Ai.dto.GeminiRequest;
import com.example.Ai_ChatBot.Ai.dto.GeminiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final WebClient geminiWebClient;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model.default}")
    private String defaultModel;

    @Value("${gemini.model.stream}")
    private String streamModel;

    public Mono<GeminiResponse> generate(GeminiRequest request) {
    return geminiWebClient
            .post()
            .uri(uriBuilder -> uriBuilder
                    .path("/models/{model}:generateContent")
                    .queryParam("key", apiKey)
                    .build(defaultModel)
            )
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(GeminiResponse.class)
            .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                    .filter(ex -> ex instanceof WebClientRequestException));
   
}

    public Flux<GeminiResponse> streamGenerate(GeminiRequest request) {
        return geminiWebClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/{model}:streamGenerateContent")
                        .queryParam("key", apiKey)
                        .queryParam("alt", "sse")
                        .build(streamModel)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(GeminiResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                        .filter(ex -> ex instanceof WebClientRequestException));
    }
}
