package com.sky.movieratingservice.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TopRatedMovieResponse(
        UUID movieId,
        String name,
        BigDecimal avgRating,
        long ratingCount
) {}
