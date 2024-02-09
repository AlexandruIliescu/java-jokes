package com.alexiliescu.javajokes.config;

import com.alexiliescu.javajokes.utils.JokeApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(JokeApiProperties jokeApiProperties) {
        return WebClient.builder()
                .baseUrl(jokeApiProperties.getBaseUrl())
                .build();
    }
}