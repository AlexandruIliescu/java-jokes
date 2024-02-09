package com.alexiliescu.javajokes.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "joke.api")
public class JokeApiProperties {

    private String baseUrl = "https://official-joke-api.appspot.com";
    private String path = "/random_joke";
}