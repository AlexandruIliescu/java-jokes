package com.alexiliescu.javajokes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class JavaJokesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaJokesApplication.class, args);
    }
}