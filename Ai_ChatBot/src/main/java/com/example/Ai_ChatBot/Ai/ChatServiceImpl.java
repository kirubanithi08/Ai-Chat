package com.example.Ai_ChatBot.Ai;

import com.example.Ai_ChatBot.Service.ChatService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final WebClient webClient;

    public ChatServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public String chat(String message) {
        try {

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", message)))
                    )
            );


            return webClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={apiKey}",
                            model, apiKey)
                    .bodyValue(requestBody)
                    .retrieve()

                    .bodyToMono(ObjectNode.class)
                    .map(jsonNode -> {

                        return jsonNode.path("candidates")
                                .get(0)
                                .path("content")
                                .path("parts")
                                .get(0)
                                .path("text")
                                .asText();
                    })
                    .block();

        } catch (WebClientResponseException e) {

            return "API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {

            return "General Error: " + e.getMessage();
        }
    }
}