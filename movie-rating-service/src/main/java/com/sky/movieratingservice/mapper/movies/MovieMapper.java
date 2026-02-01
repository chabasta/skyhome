package com.sky.movieratingservice.mapper.movies;

import com.sky.movieratingservice.api.dto.MovieResponse;
import com.sky.movieratingservice.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    public MovieResponse toResponse(Movie e) {
        return new MovieResponse(e.getId(), e.getName(), e.getCreatedAt());
    }
}

