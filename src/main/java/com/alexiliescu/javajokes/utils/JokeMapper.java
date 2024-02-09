package com.alexiliescu.javajokes.utils;

import com.alexiliescu.javajokes.model.dtos.JokeDTO;
import com.alexiliescu.javajokes.model.entities.Joke;

public class JokeMapper {

    public static JokeDTO toDto(Joke joke) {
        return new JokeDTO(joke.getId(), joke.getType(), joke.getSetup(), joke.getPunchline());
    }

    public static Joke toEntity(JokeDTO dto) {
        return new Joke(dto.id(), dto.type(), dto.setup(), dto.punchline());
    }
}