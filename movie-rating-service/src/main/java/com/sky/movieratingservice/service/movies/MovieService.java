package com.sky.movieratingservice.service.movies;

import com.sky.movieratingservice.api.dto.MovieResponse;
import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.mapper.movies.MovieMapper;
import com.sky.movieratingservice.repository.movies.MovieRepository;
import com.sky.movieratingservice.service.ranking.registry.TopRatedCalculatorRegistry;
import com.sky.movieratingservice.service.ranking.strategy.RankingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Cacheable(
            value = "topRatedMovie",
            key = "T(java.lang.String).format('%s:%d:%d:%s', #strategy.name(), #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString())",
            sync = true
    )
    public Page<TopRatedMovieResponse> getTopRated(RankingStrategy strategy, Pageable pageable) {
        return topRatedCalculatorRegistry
                .getRequired(strategy)
                .getTopRated(pageable);
    }

    public Optional<TopRatedMovieResponse> getTopRatedOne(RankingStrategy strategy) {
        return getTopRated(strategy, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }
}
