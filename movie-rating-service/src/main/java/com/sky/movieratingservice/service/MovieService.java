package com.sky.movieratingservice.service;

import com.sky.movieratingservice.api.dto.MovieResponse;
import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.mapper.MovieMapper;
import com.sky.movieratingservice.repository.MovieRepository;
import com.sky.movieratingservice.service.ranking.TopRatedCalculator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final TopRatedCalculator topRatedCalculator;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper, TopRatedCalculator topRatedCalculator) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.topRatedCalculator = topRatedCalculator;
    }

    public Page<MovieResponse> listMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(movieMapper::toResponse);
    }

    @Cacheable("topRatedMovie")
    public TopRatedMovieResponse getTopRated() {
        return topRatedCalculator
                .getTopRated()
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No ratings found"));
    }
}
