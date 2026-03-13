package com.example.Ai_ChatBot.Auth.controller;

import com.example.Ai_ChatBot.Auth.dto.AuthResponse;
import com.example.Ai_ChatBot.Auth.dto.LoginRequest;
import com.example.Ai_ChatBot.Auth.dto.RegisterRequest;
import com.example.Ai_ChatBot.Auth.service.AuthService;
import com.example.Ai_ChatBot.Common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("Registering user: {}", request.getEmail());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(authService.register(request), "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and get JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("Logging in user: {}", request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), "Login successful"));
    }
}


