package com.example.Ai_ChatBot.User.service;


import com.example.Ai_ChatBot.User.dto.UserResponse;
import com.example.Ai_ChatBot.User.dto.UserUpdateRequest;

public interface UserService {

    UserResponse getCurrentUser();

    UserResponse updateCurrentUser(UserUpdateRequest request);
}

