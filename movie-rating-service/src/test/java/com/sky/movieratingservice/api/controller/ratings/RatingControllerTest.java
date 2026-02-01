package com.sky.movieratingservice.api.controller.ratings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.movieratingservice.api.dto.RatingRequest;
import com.sky.movieratingservice.api.dto.RatingResponse;
import com.sky.movieratingservice.api.dto.RatingSummaryResponse;
import com.sky.movieratingservice.security.CurrentUser;
import com.sky.movieratingservice.security.JwtService;
import com.sky.movieratingservice.service.ratings.RatingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockitoBean
    private CurrentUser currentUser;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void addOrUpdateValidationErrorReturns400() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        RatingRequest request = new RatingRequest((short) 11);

        when(currentUser.requireUserId()).thenReturn(userId);

        mockMvc.perform(put("/api/v1/ratings/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addOrUpdateNotFoundReturns404() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        RatingRequest request = new RatingRequest((short) 7);

        when(currentUser.requireUserId()).thenReturn(userId);
        when(ratingService.addOrUpdateRating(eq(userId), eq(movieId), eq((short) 7)))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "Movie not found"));

        mockMvc.perform(put("/api/v1/ratings/{movieId}", movieId)
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

        when(currentUser.requireUserId()).thenReturn(userId);
        when(ratingService.addOrUpdateRating(eq(userId), eq(movieId), eq((short) 8)))
                .thenReturn(response);

        mockMvc.perform(put("/api/v1/ratings/{movieId}", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ratingId.toString())))
                .andExpect(jsonPath("$.userId", is(userId.toString())))
                .andExpect(jsonPath("$.movieId", is(movieId.toString())))
                .andExpect(jsonPath("$.value", is(8)));

        Mockito.verify(ratingService).addOrUpdateRating(eq(userId), eq(movieId), eq((short) 8));
    }

    @Test
    void getMyRatingReturnsSummary() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        RatingSummaryResponse response = new RatingSummaryResponse(
                movieId,
                (short) 7,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:01:00Z")
        );
        when(currentUser.requireUserId()).thenReturn(userId);
        when(ratingService.getMyRating(eq(userId), eq(movieId))).thenReturn(response);

        mockMvc.perform(get("/api/v1/ratings/{movieId}", movieId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId", is(movieId.toString())))
                .andExpect(jsonPath("$.value", is(7)));
    }

    @Test
    void listMyRatingsReturnsPage() throws Exception {
        UUID userId = UUID.randomUUID();
        RatingSummaryResponse response = new RatingSummaryResponse(
                UUID.randomUUID(),
                (short) 5,
                Instant.parse("2025-01-01T00:00:00Z"),
                Instant.parse("2025-01-01T00:01:00Z")
        );
        when(currentUser.requireUserId()).thenReturn(userId);
        when(ratingService.listMyRatings(eq(userId), any()))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/ratings/my")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].value", is(5)));
    }
}
