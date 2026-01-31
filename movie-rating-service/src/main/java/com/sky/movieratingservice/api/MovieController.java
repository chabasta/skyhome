package com.sky.movieratingservice.api;


import com.sky.movieratingservice.api.dto.MovieResponse;
import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.service.MovieService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieQueryService;

    public MovieController(MovieService movieQueryService) {
        this.movieQueryService = movieQueryService;
    }

    @GetMapping
    public Page<MovieResponse> listMovies(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return movieQueryService.listMovies(pageable);
    }

    @GetMapping("/top-rated")
    public TopRatedMovieResponse topRatedMovie() {
        return movieQueryService.getTopRated();
    }
}
