package com.alexiliescu.javajokes.unit_tests;


import com.alexiliescu.javajokes.exceptions.NoJokesAvailableException;
import com.alexiliescu.javajokes.model.dtos.JokeDTO;
import com.alexiliescu.javajokes.model.entities.Joke;
import com.alexiliescu.javajokes.respositories.JokeRepository;
import com.alexiliescu.javajokes.services.JokeServiceImpl;
import com.alexiliescu.javajokes.utils.JokeApiProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class JokeServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private JokeRepository jokeRepository;

    @Mock
    private JokeApiProperties jokeApiProperties;

    private JokeServiceImpl jokeService;

    @BeforeEach
    void setUp() {
        when(jokeApiProperties.getBaseUrl()).thenReturn("https://official-joke-api.appspot.com");
        when(jokeApiProperties.getPath()).thenReturn("/random_joke");

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        jokeService = new JokeServiceImpl(jokeRepository, jokeApiProperties, webClient);
    }

    @Test
    void fetchJokes_returnsJokesList() {
        // Given
        Joke joke1 = new Joke("1", "general", "Why did the chicken cross the road?", "To get to the other side!");
        Joke joke2 = new Joke("2", "general", "What's the best thing about Switzerland?", "I don't know, but the flag is a big plus!");
        when(responseSpec.bodyToMono(Joke.class)).thenReturn(Mono.just(joke1), Mono.just(joke2));
        when(jokeRepository.saveAll(any(List.class))).thenReturn(List.of(joke1, joke2));

        // When
        List<JokeDTO> result = jokeService.fetchJokes(2);

        // Then
        assertThat(result).hasSize(2);
        verify(jokeRepository).saveAll(any(List.class));
    }

    @Test
    void fetchJokes_withCountZero_makesNoApiCalls() {
        //Given & When
        List<JokeDTO> result = jokeService.fetchJokes(0);

        //Then
        assertThat(result).isEmpty();
        verifyNoInteractions(webClient);
    }

    @Test
    void fetchJokes_returnsCorrectlyMappedJokes() {
        // Given
        Joke apiJoke = new Joke("api1", "general", "API joke setup", "API joke punchline");
        when(responseSpec.bodyToMono(Joke.class)).thenReturn(Mono.just(apiJoke));

        // When
        List<JokeDTO> result = jokeService.fetchJokes(1);

        // Then
        assertThat(result).hasSize(1);
        JokeDTO dto = result.get(0);
        assertThat(dto.id()).isEqualTo(apiJoke.getId());
        assertThat(dto.type()).isEqualTo(apiJoke.getType());
        assertThat(dto.setup()).isEqualTo(apiJoke.getSetup());
        assertThat(dto.punchline()).isEqualTo(apiJoke.getPunchline());
    }

    @Test
    void fetchJokes_savesFetchedJokesToDatabase() {
        // Given
        Joke apiJoke = new Joke("api2", "general", "Another API joke setup", "Another API joke punchline");
        when(responseSpec.bodyToMono(Joke.class)).thenReturn(Mono.just(apiJoke));

        // When
        jokeService.fetchJokes(1);

        // Then
        verify(jokeRepository).saveAll(any());
    }

    @Test
    void fetchJokes_whenDatabaseSaveFails_propagatesException() {
        // Given & When
        Joke apiJoke = new Joke("api3", "general", "Joke setup for DB fail", "Joke punchline for DB fail");
        when(responseSpec.bodyToMono(Joke.class)).thenReturn(Mono.just(apiJoke));
        when(jokeRepository.saveAll(any())).thenThrow(new RuntimeException("Database save failed"));

        // Then
        assertThrows(RuntimeException.class, () -> jokeService.fetchJokes(1));
    }

    @Test
    void fetchJokes_mapsEntitiesToJokeDTOsCorrectly() {
        // Given
        Joke apiJoke = new Joke("api4", "humor", "setup for mapping test", "punchline for mapping test");
        when(responseSpec.bodyToMono(Joke.class)).thenReturn(Mono.just(apiJoke));

        // When
        List<JokeDTO> result = jokeService.fetchJokes(1);

        // Then
        assertThat(result).isNotEmpty().hasSize(1);
        JokeDTO jokeDTO = result.get(0);
        assertThat(jokeDTO.id()).isEqualTo(apiJoke.getId());
        assertThat(jokeDTO.type()).isEqualTo(apiJoke.getType());
        assertThat(jokeDTO.setup()).isEqualTo(apiJoke.getSetup());
        assertThat(jokeDTO.punchline()).isEqualTo(apiJoke.getPunchline());
    }

    @Test
    void fetchJokes_usesConfiguredApiPathAndBaseUrl() {
        // Given
        String expectedPath = "/random_joke";
        when(jokeApiProperties.getPath()).thenReturn(expectedPath);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(expectedPath)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Joke.class)).thenReturn(Mono.just(new Joke("1", "test", "test setup", "test punchline")));

        // When
        jokeService.fetchJokes(1);

        // Then
        verify(requestHeadersUriSpec).uri(expectedPath);
    }
}