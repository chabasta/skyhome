package com.sky.movieratingservice.repository.ratings;

import com.sky.movieratingservice.entity.Movie;
import com.sky.movieratingservice.entity.Rating;
import com.sky.movieratingservice.entity.User;
import com.sky.movieratingservice.repository.movies.MovieRepository;
import com.sky.movieratingservice.repository.users.UserRepository;
import com.sky.movieratingservice.support.PostgresContainerBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)
class RatingRepositoryIntegrationTest extends PostgresContainerBase {

    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findTopRated_ordersByAvgThenCount() {
        User u1 = saveUser("u1@example.com");
        User u2 = saveUser("u2@example.com");
        User u3 = saveUser("u3@example.com");

        Movie m1 = saveMovie("Movie One");
        Movie m2 = saveMovie("Movie Two");

        ratingRepository.save(new Rating(UUID.randomUUID(), u1, m1, (short) 9, instant("2025-01-01T00:00:00Z"), instant("2025-01-01T00:00:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), u2, m1, (short) 9, instant("2025-01-01T00:01:00Z"), instant("2025-01-01T00:01:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), u3, m2, (short) 9, instant("2025-01-01T00:02:00Z"), instant("2025-01-01T00:02:00Z")));

        Optional<com.sky.movieratingservice.repository.view.TopRatedMovieView> top =
                ratingRepository.findTopRated(org.springframework.data.domain.PageRequest.of(0, 1))
                        .stream()
                        .findFirst();

        assertThat(top).isPresent();
        assertThat(top.get().getMovieId()).isEqualTo(m1.getId());
        assertThat(top.get().getRatingsCount()).isEqualTo(2L);
    }

    @Test
    void findMostRated_ordersByCountThenAvg() {
        User u1 = saveUser("a1@example.com");
        User u2 = saveUser("a2@example.com");
        User u3 = saveUser("a3@example.com");
        User u4 = saveUser("a4@example.com");
        User u5 = saveUser("a5@example.com");

        Movie m1 = saveMovie("Movie A");
        Movie m2 = saveMovie("Movie B");
        Movie m3 = saveMovie("Movie C");

        ratingRepository.save(new Rating(UUID.randomUUID(), u1, m1, (short) 8, instant("2025-02-01T00:00:00Z"), instant("2025-02-01T00:00:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), u2, m1, (short) 8, instant("2025-02-01T00:01:00Z"), instant("2025-02-01T00:01:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), u3, m1, (short) 8, instant("2025-02-01T00:02:00Z"), instant("2025-02-01T00:02:00Z")));

        ratingRepository.save(new Rating(UUID.randomUUID(), u4, m2, (short) 9, instant("2025-02-01T00:03:00Z"), instant("2025-02-01T00:03:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), u5, m2, (short) 9, instant("2025-02-01T00:04:00Z"), instant("2025-02-01T00:04:00Z")));

        ratingRepository.save(new Rating(UUID.randomUUID(), saveUser("a6@example.com"), m3, (short) 10, instant("2025-02-01T00:05:00Z"), instant("2025-02-01T00:05:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), saveUser("a7@example.com"), m3, (short) 10, instant("2025-02-01T00:06:00Z"), instant("2025-02-01T00:06:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), saveUser("a8@example.com"), m3, (short) 10, instant("2025-02-01T00:07:00Z"), instant("2025-02-01T00:07:00Z")));

        for (int i = 0; i < 20; i++) {
            User u = saveUser("bulk" + i + "@example.com");
            Movie m = saveMovie("Bulk Movie " + i);
            ratingRepository.save(new Rating(
                    UUID.randomUUID(),
                    u,
                    m,
                    (short) 5,
                    instant("2025-02-01T01:00:00Z"),
                    instant("2025-02-01T01:00:00Z")
            ));
        }

        Optional<com.sky.movieratingservice.repository.view.TopRatedMovieView> top =
                ratingRepository.findMostRated(org.springframework.data.domain.PageRequest.of(0, 1))
                        .stream()
                        .findFirst();

        assertThat(top).isPresent();
        assertThat(top.get().getMovieId()).isEqualTo(m3.getId());
        assertThat(top.get().getRatingsCount()).isEqualTo(3L);
    }

    @Test
    void findTopRated_emptyWhenNoRatings() {
        saveMovie("Lonely Movie");

        Optional<com.sky.movieratingservice.repository.view.TopRatedMovieView> top =
                ratingRepository.findTopRated(org.springframework.data.domain.PageRequest.of(0, 1))
                        .stream()
                        .findFirst();

        assertThat(top).isEmpty();
    }

    private User saveUser(String email) {
        return userRepository.save(new User(
                UUID.randomUUID(),
                email,
                "hash",
                instant("2025-01-01T00:00:00Z")
        ));
    }

    private Movie saveMovie(String name) {
        return movieRepository.save(new Movie(
                UUID.randomUUID(),
                name,
                instant("2025-01-01T00:00:00Z")
        ));
    }

    private static Instant instant(String value) {
        return Instant.parse(value);
    }
}
