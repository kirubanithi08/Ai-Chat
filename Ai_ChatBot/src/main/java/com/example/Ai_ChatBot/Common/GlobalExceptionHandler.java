package com.example.Ai_ChatBot.Common;

import com.example.Ai_ChatBot.Common.exceptions.EmailAlreadyExistsException;
import com.example.Ai_ChatBot.Common.exceptions.InvalidCredentialsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    
    private boolean isSseRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains(MediaType.TEXT_EVENT_STREAM_VALUE);
    }

   
    private ResponseEntity<String> sseError(String message) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body("event: error\ndata: " + message + "\n\n");
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex,
                                                 HttpServletRequest request) {
        log.warn("Illegal state encountered: {}", ex.getMessage());
        if (isSseRequest(request)) {
            return sseError(ex.getMessage());
        }
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex,
                                                   HttpServletRequest request) {
        log.warn("Authentication failure: {}", ex.getMessage());
        if (isSseRequest(request)) {
            return sseError("Invalid username or password");
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid username or password"));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailExists(EmailAlreadyExistsException ex,
                                                HttpServletRequest request) {
        log.warn("Registration failed - email already exists: {}", ex.getMessage());
        if (isSseRequest(request)) {
            return sseError(ex.getMessage());
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidLogin(InvalidCredentialsException ex,
                                                 HttpServletRequest request) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        if (isSseRequest(request)) {
            return sseError(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex,
                                            HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        if (isSseRequest(request)) {
            return sseError("An unexpected error occurred. Please try again later.");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred. Please try again later."));
    }
}
