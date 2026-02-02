package com.sky.movieratingservice.service.ratings;

import com.sky.movieratingservice.api.dto.RatingResponse;
import com.sky.movieratingservice.api.dto.RatingSummaryResponse;
import com.sky.movieratingservice.entity.Rating;
import com.sky.movieratingservice.mapper.ratings.RatingMapper;
import com.sky.movieratingservice.events.RatingChangedEvent;
import com.sky.movieratingservice.repository.movies.MovieRepository;
import com.sky.movieratingservice.repository.ratings.RatingRepository;
import com.sky.movieratingservice.repository.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingService.class);

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

    @Transactional
    public RatingResponse addOrUpdateRating(UUID userId, UUID movieId, short value) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResponseStatusException(NOT_FOUND, "Movie not found");
        }
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(NOT_FOUND, "User not found");
        }

        Instant now = Instant.now();
        ratingRepository.upsert(UUID.randomUUID(), userId, movieId, value, now);
        eventPublisher.publishEvent(new RatingChangedEvent(movieId));
        log.info("Rating upsert userId={} movieId={} value={}", userId, movieId, value);
        return ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .map(ratingMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rating not found"));
    }

    public void deleteRating(UUID userId, UUID movieId) {
        Rating rating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rating not found"));
        ratingRepository.delete(rating);
        eventPublisher.publishEvent(new RatingChangedEvent(movieId));
        log.info("Rating deleted userId={} movieId={}", userId, movieId);
    }

    public RatingSummaryResponse getMyRating(UUID userId, UUID movieId) {
        Rating rating = ratingRepository.findByUserIdAndMovieId(userId, movieId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Rating not found"));
        return new RatingSummaryResponse(
                rating.getMovie().getId(),
                rating.getValue(),
                rating.getCreatedAt(),
                rating.getUpdatedAt()
        );
    }

    public Page<RatingSummaryResponse> listMyRatings(UUID userId,
                                                     Pageable pageable) {
        return ratingRepository.findAllByUserId(userId, pageable)
                .map(rating -> new RatingSummaryResponse(
                        rating.getMovie().getId(),
                        rating.getValue(),
                        rating.getCreatedAt(),
                        rating.getUpdatedAt()
                ));
    }
}
