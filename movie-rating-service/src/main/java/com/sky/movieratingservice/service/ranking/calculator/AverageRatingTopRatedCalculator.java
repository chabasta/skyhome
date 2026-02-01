package com.sky.movieratingservice.service.ranking.calculator;

import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.repository.ratings.RatingRepository;
import com.sky.movieratingservice.service.ranking.strategy.RankingStrategy;
import com.sky.movieratingservice.service.ranking.strategy.TopRatedCalculator;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Component
public class AverageRatingTopRatedCalculator implements TopRatedCalculator {

    private final RatingRepository ratingRepository;

    public AverageRatingTopRatedCalculator(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public RankingStrategy getKey() {
        return RankingStrategy.AVERAGE;
    }

    @Override
    public Optional<TopRatedMovieResponse> getTopRated() {
        return ratingRepository.findTopRated(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(view -> new TopRatedMovieResponse(
                        view.getMovieId(),
                        view.getMovieName(),
                        BigDecimal.valueOf(view.getAvgRating()).setScale(2, RoundingMode.HALF_UP),
                        view.getRatingsCount()
                ));
    }
}
