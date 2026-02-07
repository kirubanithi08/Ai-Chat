package com.example.Ai_ChatBot.User.service;

import com.example.Ai_ChatBot.User.dto.UserResponse;
import com.example.Ai_ChatBot.User.dto.UserUpdateRequest;
import com.example.Ai_ChatBot.User.entity.User;
import com.example.Ai_ChatBot.User.repository.UserRepository;
import com.example.Ai_ChatBot.Common.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser() {
        User user = SecurityUtils.getCurrentUser();
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateCurrentUser(UserUpdateRequest request) {
        User user = SecurityUtils.getCurrentUser();

        user.setName(request.getName());
        userRepository.save(user);

        return mapToResponse(user);
    }


    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
