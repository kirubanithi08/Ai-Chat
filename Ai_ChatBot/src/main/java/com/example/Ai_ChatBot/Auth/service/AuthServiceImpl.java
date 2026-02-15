package com.example.Ai_ChatBot.Auth.service;

import com.example.Ai_ChatBot.Auth.dto.AuthResponse;
import com.example.Ai_ChatBot.Auth.dto.LoginRequest;
import com.example.Ai_ChatBot.Auth.dto.RegisterRequest;
import com.example.Ai_ChatBot.Common.exceptions.EmailAlreadyExistsException;
import com.example.Ai_ChatBot.Common.exceptions.InvalidCredentialsException;
import com.example.Ai_ChatBot.Security.JwtTokenProvider;
import com.example.Ai_ChatBot.User.entity.User;
import com.example.Ai_ChatBot.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail());

        return new AuthResponse(token, user.getName());
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid email or password");
        }


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        String token = jwtTokenProvider.generateToken(user.getEmail());

        return new AuthResponse(token, user.getName());
    }
}
