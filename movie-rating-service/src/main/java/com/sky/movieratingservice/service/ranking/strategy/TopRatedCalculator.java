package com.sky.movieratingservice.service.ranking.strategy;

import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TopRatedCalculator {
    RankingStrategy getKey();
    Page<TopRatedMovieResponse> getTopRated(Pageable pageable);
}
