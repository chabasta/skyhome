package com.sky.movieratingservice.service.ranking;

import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;

import java.util.Optional;

public interface TopRatedCalculator {
    RankingStrategy getKey();
    Optional<TopRatedMovieResponse> getTopRated();
}
