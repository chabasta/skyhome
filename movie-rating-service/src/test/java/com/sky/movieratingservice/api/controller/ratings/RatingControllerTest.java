package com.sky.movieratingservice.api.controller.ratings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.movieratingservice.api.dto.RatingRequest;
import com.sky.movieratingservice.api.dto.RatingResponse;
import com.sky.movieratingservice.service.ratings.RatingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingController.class)
@AutoConfigureMockMvc(addFilters = false)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RatingService ratingService;

    @Test
    void addOrUpdateValidationErrorReturns400() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        RatingRequest request = new RatingRequest((short) 11);

        mockMvc.perform(put("/api/v1/ratings/{movieId}", movieId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOrUpdateNotFoundReturns404() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        RatingRequest request = new RatingRequest((short) 7);

        when(ratingService.addOrUpdateRating(eq(userId), eq(movieId), eq((short) 7)))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Movie not found"));

        mockMvc.perform(put("/api/v1/ratings/{movieId}", movieId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addOrUpdateHappyPathReturnsResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        UUID ratingId = UUID.randomUUID();
        RatingRequest request = new RatingRequest((short) 8);

        RatingResponse response = new RatingResponse(
                ratingId,
                userId,
                movieId,
                (short) 8,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:01:00Z")
        );

        when(ratingService.addOrUpdateRating(eq(userId), eq(movieId), eq((short) 8)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/ratings/{movieId}", movieId)
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ratingId.toString())))
                .andExpect(jsonPath("$.userId", is(userId.toString())))
                .andExpect(jsonPath("$.movieId", is(movieId.toString())))
                .andExpect(jsonPath("$.value", is(8)));

        Mockito.verify(ratingService).addOrUpdateRating(eq(userId), eq(movieId), eq((short) 8));
    }
}
