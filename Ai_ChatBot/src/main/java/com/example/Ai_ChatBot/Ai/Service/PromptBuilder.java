package com.example.Ai_ChatBot.Ai.Service;

import com.example.Ai_ChatBot.Chat.Entity.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    public String buildPrompt(List<ChatMessage> history) {

        StringBuilder prompt = new StringBuilder();


        prompt.append("""
        You are a helpful AI assistant inside a chatbot application.
        Answer clearly and concisely.

        Rules:
        - Be accurate
        - Provide examples if the question is technical
        - If you don't know the answer, say you don't know

        Conversation:
        """);


        for (ChatMessage message : history) {

            if (message.getSender() == ChatMessage.Sender.USER) {
                prompt.append("\nUser: ")
                        .append(message.getContent());
            } else {
                prompt.append("\nAssistant: ")
                        .append(message.getContent());
            }
        }


        prompt.append("\nAssistant:");

        return prompt.toString();
    }
}
