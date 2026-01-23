package com.example.Ai_ChatBot.Ai;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

public class ChatServiceImp {
    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final WebClient webClient;

    public ChatServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    Map<String, Object>requestBody=Map.of(
            "contents", List.of(
                    Map.of(
                            "parts",List.of(
                                    Map.of("text",message)
                            )
                    )
            )
    );
}
