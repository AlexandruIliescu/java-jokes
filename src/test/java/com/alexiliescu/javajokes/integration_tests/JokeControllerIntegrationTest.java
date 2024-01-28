package com.alexiliescu.javajokes.integration_tests;

import com.alexiliescu.javajokes.model.dtos.JokeDTO;
import com.alexiliescu.javajokes.services.JokeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class JokeControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private JokeService jokeService;

    @BeforeEach
    void setUp() {
        when(jokeService.fetchJokes(anyInt()))
                .thenAnswer(invocation -> {
                    Integer count = invocation.getArgument(0);
                    return Flux.range(1, count)
                            .map(i -> new JokeDTO(String.valueOf(i), "General", "Joke " + i, "Punchline " + i))
                            .collectList()
                            .block();
                });
    }

    @Test
    void getJokes_returnsJokesList() {
        webTestClient.get().uri("/api/jokes?count=3")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(JokeDTO.class)
                .hasSize(3);
    }

    @Test
    void getJokes_withNegativeCount_returnsBadRequest() {
        webTestClient.get().uri("/api/jokes?count=-1")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getJokes_withExceedingCountValue_returnsBadRequest() {
        webTestClient.get().uri("/api/jokes?count=101")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getJokes_withoutCount_usesDefaultValueOf5() {
        webTestClient.get().uri("/api/jokes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(JokeDTO.class)
                .hasSize(5);
    }

    @Test
    void getJokes_withNonIntegerCount_returnsBadRequest() {
        webTestClient.get().uri("/api/jokes?count=abc")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getJokes_withSpecificCount_returnsCorrectNumberOfJokes() {
        int requestedCount = 10;

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/jokes")
                        .queryParam("count", requestedCount)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(JokeDTO.class)
                .hasSize(requestedCount);
    }
}