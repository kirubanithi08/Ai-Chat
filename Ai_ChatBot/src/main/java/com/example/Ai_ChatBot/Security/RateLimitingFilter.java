package com.example.Ai_ChatBot.Security;

import com.example.Ai_ChatBot.Common.ApiResponse;
import com.example.Ai_ChatBot.Config.RateLimitConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/chat")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String identifier = (authentication != null) ? authentication.getName() : request.getRemoteAddr();

            Bucket bucket = rateLimitConfig.resolveBucket(identifier);
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (probe.isConsumed()) {
                response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            } else {
                log.warn("Rate limit exceeded for user/IP: {}", identifier);
                long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;
                response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
                
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                
                ApiResponse<Object> apiResponse = ApiResponse.error("Rate limit exceeded. Try again in " + waitForRefill + " seconds.");
                response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
