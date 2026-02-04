package com.example.Ai_ChatBot.Config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient geminiWebClient() {

        HttpClient httpClient = HttpClient.create()
                .protocol(HttpProtocol.HTTP11) // 🔴 CRITICAL
                .responseTimeout(Duration.ofSeconds(30))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

        return WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}

