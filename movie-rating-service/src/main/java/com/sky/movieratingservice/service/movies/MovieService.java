package com.sky.movieratingservice.service.movies;

import com.sky.movieratingservice.api.dto.MovieResponse;
import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.mapper.movies.MovieMapper;
import com.sky.movieratingservice.repository.movies.MovieRepository;
import com.sky.movieratingservice.repository.ratings.RatingRepository;
import com.sky.movieratingservice.service.ranking.RankingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final RatingRepository ratingRepository;

    public MovieService(MovieRepository movieRepository, MovieMapper movieMapper, RatingRepository ratingRepository) {
        this.movieRepository = movieRepository;
        this.movieMapper = movieMapper;
        this.ratingRepository = ratingRepository;
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
        return switch (strategy) {
            case AVERAGE -> ratingRepository.findTopRated(pageable).map(this::toResponse);
            case MOST_RATED -> ratingRepository.findMostRated(pageable).map(this::toResponse);
        };
    }

    private TopRatedMovieResponse toResponse(com.sky.movieratingservice.repository.view.TopRatedMovieView view) {
        return new TopRatedMovieResponse(
                view.getMovieId(),
                view.getMovieName(),
                java.math.BigDecimal.valueOf(view.getAvgRating()).setScale(2, java.math.RoundingMode.HALF_UP),
                view.getRatingsCount()
        );
    }
}

