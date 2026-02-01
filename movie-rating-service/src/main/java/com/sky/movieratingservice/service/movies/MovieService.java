package com.sky.movieratingservice.service.movies;

import com.sky.movieratingservice.api.dto.MovieResponse;
import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.mapper.movies.MovieMapper;
import com.sky.movieratingservice.repository.movies.MovieRepository;
import com.sky.movieratingservice.service.ranking.RankingStrategy;
import com.sky.movieratingservice.service.ranking.TopRatedCalculatorRegistry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final TopRatedCalculatorRegistry topRatedCalculatorRegistry;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper, TopRatedCalculatorRegistry topRatedCalculatorRegistry) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.topRatedCalculatorRegistry = topRatedCalculatorRegistry;
    }

    public Page<MovieResponse> listMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(movieMapper::toResponse);
    }

    @Cacheable(value = "topRatedMovie", key = "#strategy.name()")
    public TopRatedMovieResponse getTopRated(RankingStrategy strategy) {
        try {
            return topRatedCalculatorRegistry
                    .getRequired(strategy)
                    .getTopRated()
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No ratings found"));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, ex.getMessage());
        }
    }
}

