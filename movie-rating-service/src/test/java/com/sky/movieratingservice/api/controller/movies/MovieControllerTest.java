package com.sky.movieratingservice.api.controller.movies;

import com.sky.movieratingservice.api.dto.MovieResponse;
import com.sky.movieratingservice.api.dto.TopRatedMovieResponse;
import com.sky.movieratingservice.service.movies.MovieService;
import com.sky.movieratingservice.service.ranking.RankingStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@AutoConfigureMockMvc(addFilters = false)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieService movieService;

    @Test
    void listMoviesReturnsPage() throws Exception {
        MovieResponse movie = new MovieResponse(
                UUID.randomUUID(),
                "The Movie",
                Instant.parse("2025-01-01T00:00:00Z")
        );
        Page<MovieResponse> page = new PageImpl<>(
                List.of(movie),
                PageRequest.of(0, 20),
                1
        );
        when(movieService.listMovies(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/movies")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", is("The Movie")));
    }

    @Test
    void topRatedEmptyReturnsEmptyPage() throws Exception {
        when(movieService.getTopRated(eq(RankingStrategy.AVERAGE), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(get("/api/v1/movies/top-rated")
                        .param("strategy", "AVERAGE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void topRatedHappyPathReturnsResponse() throws Exception {
        UUID movieId = UUID.randomUUID();
        TopRatedMovieResponse response = new TopRatedMovieResponse(
                movieId,
                "Best Movie",
                BigDecimal.valueOf(9.75),
                100L
        );
        Page<TopRatedMovieResponse> page = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 20),
                1
        );
        when(movieService.getTopRated(eq(RankingStrategy.AVERAGE), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/movies/top-rated")
                        .param("strategy", "AVERAGE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].movieId", is(movieId.toString())))
                .andExpect(jsonPath("$.content[0].name", is("Best Movie")))
                .andExpect(jsonPath("$.content[0].avgRating", is(9.75)))
                .andExpect(jsonPath("$.content[0].ratingCount", is(100)));

        Mockito.verify(movieService).getTopRated(eq(RankingStrategy.AVERAGE), any());
    }
}
