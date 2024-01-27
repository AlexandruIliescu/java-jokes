package com.alexiliescu.javajokes.controllers;

import com.alexiliescu.javajokes.model.dtos.JokeDTO;
import com.alexiliescu.javajokes.services.JokeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JokeController {

    private final JokeService jokeService;

    public JokeController(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    @GetMapping("/api/jokes")
    public ResponseEntity<List<JokeDTO>> getJokes(@RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(jokeService.fetchJokes(count));
    }
}