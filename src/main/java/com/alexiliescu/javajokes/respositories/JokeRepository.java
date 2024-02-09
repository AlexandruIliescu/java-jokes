package com.alexiliescu.javajokes.respositories;

import com.alexiliescu.javajokes.model.entities.Joke;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JokeRepository extends MongoRepository<Joke, String> {

}