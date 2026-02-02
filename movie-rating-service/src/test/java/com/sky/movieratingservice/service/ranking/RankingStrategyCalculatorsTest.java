package com.sky.movieratingservice.service.ranking;

import com.sky.movieratingservice.service.ranking.calculator.AverageRatingTopRatedCalculator;
import com.sky.movieratingservice.service.ranking.calculator.MostRatedTopRatedCalculator;
import com.sky.movieratingservice.service.ranking.strategy.RankingStrategy;
import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.repository.ratings.RatingRepository;
import com.sky.movieratingservice.repository.view.TopRatedMovieView;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RankingStrategyCalculatorsTest {

    @Test
    void averageRatingCalculator_roundsToTwoDecimals() {
        RatingRepository ratingRepository = mock(RatingRepository.class);
        when(ratingRepository.findTopRated(any(Pageable.class))).thenReturn(new PageImpl<>(java.util.List.of(view(
                UUID.randomUUID(),
                "Movie",
                8.666,
                12L
        ))));

        AverageRatingTopRatedCalculator calculator = new AverageRatingTopRatedCalculator(ratingRepository);

        assertThat(calculator.getKey()).isEqualTo(RankingStrategy.AVERAGE);
        Page<TopRatedMovieResponse> page = calculator.getTopRated(PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        TopRatedMovieResponse result = page.getContent().get(0);
        assertThat(result.avgRating().toString()).isEqualTo("8.67");
        assertThat(result.ratingCount()).isEqualTo(12L);
    }

    @Test
    void mostRatedCalculator_passesThroughViewData() {
        RatingRepository ratingRepository = mock(RatingRepository.class);
        UUID movieId = UUID.randomUUID();
        when(ratingRepository.findMostRated(any(Pageable.class))).thenReturn(new PageImpl<>(java.util.List.of(view(
                movieId,
                "Popular Movie",
                9.125,
                42L
        ))));

        MostRatedTopRatedCalculator calculator = new MostRatedTopRatedCalculator(ratingRepository);

        assertThat(calculator.getKey()).isEqualTo(RankingStrategy.MOST_RATED);
        Page<TopRatedMovieResponse> page = calculator.getTopRated(PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        TopRatedMovieResponse result = page.getContent().get(0);
        assertThat(result.movieId()).isEqualTo(movieId);
        assertThat(result.ratingCount()).isEqualTo(42L);
        assertThat(result.avgRating().toString()).isEqualTo("9.13");
    }

    @Test
    void calculatorReturnsEmptyWhenRepositoryEmpty() {
        RatingRepository ratingRepository = mock(RatingRepository.class);
        when(ratingRepository.findTopRated(any(Pageable.class))).thenReturn(Page.empty());

        AverageRatingTopRatedCalculator calculator = new AverageRatingTopRatedCalculator(ratingRepository);

        assertThat(calculator.getTopRated(PageRequest.of(0, 10)).getContent()).isEmpty();
    }

    private static TopRatedMovieView view(UUID movieId, String name, Double avgRating, Long count) {
        return new TopRatedMovieView() {
            @Override
            public UUID getMovieId() {
                return movieId;
            }

            @Override
            public String getMovieName() {
                return name;
            }

            @Override
            public Double getAvgRating() {
                return avgRating;
            }

            @Override
            public Long getRatingsCount() {
                return count;
            }
        };
    }
}
