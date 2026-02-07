package com.example.Ai_ChatBot.User.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @NotBlank
    private String name;
}

