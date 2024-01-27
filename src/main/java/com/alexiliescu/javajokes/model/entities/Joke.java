package com.alexiliescu.javajokes.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Data
@Document(collection = "jokes")
public class Joke {

    @Id
    private String id;
    private String type;
    private String setup;
    private String punchline;
}