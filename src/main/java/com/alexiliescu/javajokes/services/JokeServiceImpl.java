package com.alexiliescu.javajokes.services;

import com.alexiliescu.javajokes.exceptions.NoJokesAvailableException;
import com.alexiliescu.javajokes.model.dtos.JokeDTO;
import com.alexiliescu.javajokes.model.entities.Joke;
import com.alexiliescu.javajokes.respositories.JokeRepository;
import com.alexiliescu.javajokes.utils.JokeApiProperties;
import com.alexiliescu.javajokes.utils.JokeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static java.util.Collections.emptyList;

@Slf4j
@Service
public class JokeServiceImpl implements JokeService {

    private static final int BATCH_SIZE = 10;

    private final WebClient webClient;
    private final JokeRepository jokeRepository;
    private final String jokeApiPath;

    public JokeServiceImpl(JokeRepository jokeRepository,
                           JokeApiProperties jokeApiProperties,
                           WebClient webClient) {
        this.jokeRepository = jokeRepository;
        this.jokeApiPath = jokeApiProperties.getPath();
        this.webClient = webClient;
    }

    /**
     * Fetches a list of jokes, respecting the maximum count. Jokes are fetched in batches of 10.
     *
     * @param count The desired number of jokes to fetch, not exceeding 100.
     * @return A list of JokeDTO objects.
     */
    @Override
    @Retryable(value = WebClientResponseException.class, maxAttempts = 3, backoff = @Backoff(delay = 2000))
    public List<JokeDTO> fetchJokes(int count) {
        List<JokeDTO> jokes = Flux.range(0, (int) Math.ceil((double) count / BATCH_SIZE))
                .flatMap(batchIndex -> fetchJokeBatch(Math.min(BATCH_SIZE, count - batchIndex * BATCH_SIZE)))
                .map(JokeMapper::toDto)
                .collectList()
                .blockOptional().orElse(emptyList());

        List<Joke> jokeEntities = jokes.stream()
                .map(JokeMapper::toEntity)
                .toList();
        jokeRepository.saveAll(jokeEntities);
        log.info("{} jokes were saved in the database.", count);

        return jokes;
    }

    private Flux<Joke> fetchJokeBatch(int batchCount) {
        return Flux.range(0, batchCount)
                .flatMap(i -> fetchSingleJoke())
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Joke> fetchSingleJoke() {
        return webClient.get()
                .uri(jokeApiPath)
                .retrieve()
                .bodyToMono(Joke.class);
    }

    /**
     * Provides a fallback method for when joke fetching fails after retries.
     * Attempts to fetch jokes from the database. If none are found, throws NoJokesAvailableException exception.
     *
     * @param exception The exception that caused the fallback.
     * @param count     The number of jokes initially requested.
     * @return A list of JokeDTO objects from the database.
     * @throws NoJokesAvailableException if no jokes are available in the database.
     */
    @Recover
    public List<JokeDTO> recover(WebClientException exception, int count) {
        Pageable limit = PageRequest.of(0, count);
        List<Joke> jokes = jokeRepository.findAll(limit).getContent();

        if (jokes.isEmpty()) {
            log.info("Recover method triggered, but there are no jokes saved in the database.");
            throw new NoJokesAvailableException("No jokes are available right now.");
        }

        int retrievedJokesCount = jokes.size();
        if (retrievedJokesCount < count) {
            log.info("Requested {} jokes, but only {} were found in the database by the recover method.", count, retrievedJokesCount);
        } else {
            log.info("{} jokes were found in the database by the recover method and retrieved.", count);
        }

        return jokes.stream()
                .map(JokeMapper::toDto)
                .toList();
    }
}