package com.alexiliescu.javajokes.services;

import com.alexiliescu.javajokes.model.dtos.JokeDTO;

import java.util.List;

public interface JokeService {

    List<JokeDTO> fetchJokes(int count);
}