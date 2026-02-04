package com.example.Ai_ChatBot.Auth.service;

import com.example.Ai_ChatBot.Auth.dto.AuthResponse;
import com.example.Ai_ChatBot.Auth.dto.LoginRequest;
import com.example.Ai_ChatBot.Auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}

