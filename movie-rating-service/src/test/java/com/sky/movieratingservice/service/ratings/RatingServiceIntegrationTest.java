package com.sky.movieratingservice.service.ratings;

import com.sky.movieratingservice.entity.Movie;
import com.sky.movieratingservice.entity.User;
import com.sky.movieratingservice.repository.movies.MovieRepository;
import com.sky.movieratingservice.repository.ratings.RatingRepository;
import com.sky.movieratingservice.repository.users.UserRepository;
import com.sky.movieratingservice.support.PostgresContainerBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RatingServiceIntegrationTest extends PostgresContainerBase {

    @Autowired
    private RatingService ratingService;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void upsertCreatesSingleRowAndUpdatesValue() {
        User user = saveUser("user@example.com");
        Movie movie = saveMovie("Upsert Movie");

        ratingService.addOrUpdateRating(user.getId(), movie.getId(), (short) 7);
        ratingService.addOrUpdateRating(user.getId(), movie.getId(), (short) 9);

        assertThat(ratingRepository.count()).isEqualTo(1);
        assertThat(ratingRepository.findByUserIdAndMovieId(user.getId(), movie.getId()))
                .isPresent()
                .get()
                .satisfies(rating -> {
                    assertThat(rating.getValue()).isEqualTo((short) 9);
                    assertThat(rating.getCreatedAt()).isNotNull();
                    assertThat(rating.getUpdatedAt()).isNotNull();
                    assertThat(rating.getUpdatedAt()).isAfterOrEqualTo(rating.getCreatedAt());
                });
    }

    @Test
    void deleteRemovesRating() {
        User user = saveUser("del@example.com");
        Movie movie = saveMovie("Delete Movie");

        ratingService.addOrUpdateRating(user.getId(), movie.getId(), (short) 6);
        ratingService.deleteRating(user.getId(), movie.getId());

        assertThat(ratingRepository.findByUserIdAndMovieId(user.getId(), movie.getId()))
                .isEmpty();
    }

    @Test
    void deleteMissingThrowsNotFound() {
        User user = saveUser("missing@example.com");
        Movie movie = saveMovie("Missing Movie");

        assertThatThrownBy(() -> ratingService.deleteRating(user.getId(), movie.getId()))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(NOT_FOUND);
    }

    private User saveUser(String email) {
        return userRepository.save(new User(
                UUID.randomUUID(),
                email,
                "hash",
                Instant.parse("2025-01-01T00:00:00Z")
        ));
    }

    private Movie saveMovie(String name) {
        return movieRepository.save(new Movie(
                UUID.randomUUID(),
                name,
                Instant.parse("2025-01-01T00:00:00Z")
        ));
    }
}
