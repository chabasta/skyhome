package com.sky.movieratingservice.service.ranking;

import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.repository.ratings.RatingRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

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
    public Optional<TopRatedMovieResponse> getTopRated() {
        return ratingRepository.findMostRated().map(view -> new TopRatedMovieResponse(
                view.getMovieId(),
                view.getMovieName(),
                BigDecimal.valueOf(view.getAvgRating()).setScale(2, RoundingMode.HALF_UP),
                view.getRatingsCount()
        ));
    }
}
