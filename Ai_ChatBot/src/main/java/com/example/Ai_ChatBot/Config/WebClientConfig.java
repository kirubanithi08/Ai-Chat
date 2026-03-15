package com.example.Ai_ChatBot.Config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private static final int MB = 1024 * 1024;

    @Bean
    public WebClient geminiWebClient() {

        HttpClient httpClient = HttpClient.create()
                .protocol(HttpProtocol.HTTP11)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .responseTimeout(Duration.ofSeconds(10))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(120, TimeUnit.SECONDS))  // streaming can be long
                        .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS))
                );


        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(10 * MB))
                .build();

        return WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(exchangeStrategies)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}