package com.example.Ai_ChatBot.User.controller;

import com.example.Ai_ChatBot.Common.ApiResponse;
import com.example.Ai_ChatBot.User.dto.UserResponse;
import com.example.Ai_ChatBot.User.dto.UserUpdateRequest;
import com.example.Ai_ChatBot.User.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        log.debug("Fetching current user profile");
        return ResponseEntity.ok(ApiResponse.success(userService.getCurrentUser()));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current authenticated user profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @Valid @RequestBody UserUpdateRequest request
    ) {
        log.info("Updating current user profile");
        return ResponseEntity.ok(ApiResponse.success(userService.updateCurrentUser(request), "Profile updated successfully"));
    }
}


