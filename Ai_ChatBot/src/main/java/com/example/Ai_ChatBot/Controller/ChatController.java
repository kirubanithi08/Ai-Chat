package com.example.Ai_ChatBot.Controller;

import com.example.Ai_ChatBot.Dto.ChatRequest;
import com.example.Ai_ChatBot.Service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController{
private final ChatService chatService;

public ChatController(ChatService chatService){
    this.chatService=chatService;
}
    @PostMapping("/chat")
    ResponseEntity<?>UserChat(@RequestBody ChatRequest request){
     String aiReplay=chatService.chat(request.getMessage());
     return ResponseEntity.ok(aiReplay);
    }

}