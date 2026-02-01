package com.sky.movieratingservice.repository.ratings;

import com.sky.movieratingservice.entity.Movie;
import com.sky.movieratingservice.entity.Rating;
import com.sky.movieratingservice.entity.User;
import com.sky.movieratingservice.repository.movies.MovieRepository;
import com.sky.movieratingservice.repository.users.UserRepository;
import com.sky.movieratingservice.repository.view.TopRatedMovieView;
import com.sky.movieratingservice.support.PostgresContainerBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

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
        User user1 = saveUser("user1@example.com");
        User user2 = saveUser("user2@example.com");
        User user3 = saveUser("user3@example.com");

        Movie movie1 = saveMovie("Movie One");
        Movie movie2 = saveMovie("Movie Two");

        ratingRepository.save(new Rating(UUID.randomUUID(), user1, movie1, (short) 9, instant("2025-01-01T00:00:00Z"), instant("2025-01-01T00:00:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), user2, movie1, (short) 9, instant("2025-01-01T00:01:00Z"), instant("2025-01-01T00:01:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), user3, movie2, (short) 9, instant("2025-01-01T00:02:00Z"), instant("2025-01-01T00:02:00Z")));

        Optional<TopRatedMovieView> top =
                ratingRepository.findTopRated(PageRequest.of(0, 1))
                        .stream()
                        .findFirst();

        assertThat(top).isPresent();
        assertThat(top.get().getMovieId()).isEqualTo(movie1.getId());
        assertThat(top.get().getRatingsCount()).isEqualTo(2L);
    }

    @Test
    void findMostRated_ordersByCountThenAvg() {
        User user1 = saveUser("a1@example.com");
        User user2 = saveUser("a2@example.com");
        User user3 = saveUser("a3@example.com");
        User user4 = saveUser("a4@example.com");
        User user5 = saveUser("a5@example.com");

        Movie movie1 = saveMovie("Movie A");
        Movie movie2 = saveMovie("Movie B");
        Movie movie3 = saveMovie("Movie C");

        ratingRepository.save(new Rating(UUID.randomUUID(), user1, movie1, (short) 8, instant("2025-02-01T00:00:00Z"), instant("2025-02-01T00:00:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), user2, movie1, (short) 8, instant("2025-02-01T00:01:00Z"), instant("2025-02-01T00:01:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), user3, movie1, (short) 8, instant("2025-02-01T00:02:00Z"), instant("2025-02-01T00:02:00Z")));

        ratingRepository.save(new Rating(UUID.randomUUID(), user4, movie2, (short) 9, instant("2025-02-01T00:03:00Z"), instant("2025-02-01T00:03:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), user5, movie2, (short) 9, instant("2025-02-01T00:04:00Z"), instant("2025-02-01T00:04:00Z")));

        ratingRepository.save(new Rating(UUID.randomUUID(), saveUser("a6@example.com"), movie3, (short) 10, instant("2025-02-01T00:05:00Z"), instant("2025-02-01T00:05:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), saveUser("a7@example.com"), movie3, (short) 10, instant("2025-02-01T00:06:00Z"), instant("2025-02-01T00:06:00Z")));
        ratingRepository.save(new Rating(UUID.randomUUID(), saveUser("a8@example.com"), movie3, (short) 10, instant("2025-02-01T00:07:00Z"), instant("2025-02-01T00:07:00Z")));

        for (int i = 0; i < 20; i++) {
            User user = saveUser("bulk" + i + "@example.com");
            Movie movie = saveMovie("Bulk Movie " + i);
            ratingRepository.save(new Rating(
                    UUID.randomUUID(),
                    user,
                    movie,
                    (short) 5,
                    instant("2025-02-01T01:00:00Z"),
                    instant("2025-02-01T01:00:00Z")
            ));
        }

        Optional<TopRatedMovieView> top =
                ratingRepository.findMostRated(PageRequest.of(0, 1))
                        .stream()
                        .findFirst();

        assertThat(top).isPresent();
        assertThat(top.get().getMovieId()).isEqualTo(movie3.getId());
        assertThat(top.get().getRatingsCount()).isEqualTo(3L);
    }

    @Test
    void findTopRated_emptyWhenNoRatings() {
        saveMovie("Lonely Movie");

        Optional<TopRatedMovieView> top =
                ratingRepository.findTopRated(PageRequest.of(0, 1))
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
