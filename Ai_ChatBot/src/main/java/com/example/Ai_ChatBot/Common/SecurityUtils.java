package com.example.Ai_ChatBot.Common;

import com.example.Ai_ChatBot.User.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {

    private SecurityUtils() {

    }


    public static User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new IllegalStateException("Invalid authentication principal");
        }

        return (User) principal;
    }


    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}

