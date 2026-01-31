package com.sky.movieratingservice.service;

import com.sky.movieratingservice.api.dto.RatingResponse;
import com.sky.movieratingservice.entity.Movie;
import com.sky.movieratingservice.entity.Rating;
import com.sky.movieratingservice.entity.User;
import com.sky.movieratingservice.events.RatingChangedEvent;
import com.sky.movieratingservice.mapper.RatingMapper;
import com.sky.movieratingservice.repository.MovieRepository;
import com.sky.movieratingservice.repository.RatingRepository;
import com.sky.movieratingservice.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final RatingMapper ratingMapper;
    private final ApplicationEventPublisher eventPublisher;

    public RatingService(
            RatingRepository ratingRepository,
            MovieRepository movieRepository,
            UserRepository userRepository,
            RatingMapper ratingMapper,
            ApplicationEventPublisher eventPublisher
    ) {
        this.ratingRepository = ratingRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.ratingMapper = ratingMapper;
        this.eventPublisher = eventPublisher;
    }

    public RatingResponse addOrUpdateRating(UUID userId, UUID movieId, short value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Movie not found"));

        Rating rating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .map(existing -> {
                    existing.setValue(value);
                    return existing;
                })
                .orElseGet(() -> new Rating(
                        UUID.randomUUID(),
                        user,
                        movie,
                        value,
                        Instant.now(),
                        Instant.now()
                ));

        Rating saved = ratingRepository.save(rating);
        eventPublisher.publishEvent(new RatingChangedEvent(movieId));
        return ratingMapper.toResponse(saved);
    }

    public void deleteRating(UUID userId, UUID movieId) {
        Rating rating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rating not found"));
        ratingRepository.delete(rating);
        eventPublisher.publishEvent(new RatingChangedEvent(movieId));
    }
}
