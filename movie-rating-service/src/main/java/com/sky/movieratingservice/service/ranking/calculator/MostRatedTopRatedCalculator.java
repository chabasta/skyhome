package com.sky.movieratingservice.service.ranking.calculator;

import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.repository.ratings.RatingRepository;
import com.sky.movieratingservice.service.ranking.strategy.RankingStrategy;
import com.sky.movieratingservice.service.ranking.strategy.TopRatedCalculator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class MostRatedTopRatedCalculator implements TopRatedCalculator {

    private final RatingRepository ratingRepository;

    public MostRatedTopRatedCalculator(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    @Override
    public RankingStrategy getKey() {
        return RankingStrategy.MOST_RATED;
    }

    @Override
    public Page<TopRatedMovieResponse> getTopRated(Pageable pageable) {
        return ratingRepository.findMostRated(pageable)
                .map(view -> new TopRatedMovieResponse(
                        view.getMovieId(),
                        view.getMovieName(),
                        BigDecimal.valueOf(view.getAvgRating()).setScale(2, RoundingMode.HALF_UP),
                        view.getRatingsCount()
                ));
    }
}
