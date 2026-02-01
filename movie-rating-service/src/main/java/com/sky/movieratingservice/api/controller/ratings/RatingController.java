package com.sky.movieratingservice.api.controller.ratings;

import com.sky.movieratingservice.api.dto.RatingRequest;
import com.sky.movieratingservice.api.dto.RatingResponse;
import com.sky.movieratingservice.service.ratings.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PutMapping("/{movieId}")
    public RatingResponse addOrUpdate(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID movieId,
            @Valid @RequestBody RatingRequest request
    ) {
        return ratingService.addOrUpdateRating(userId, movieId, request.value());
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID movieId
    ) {
        ratingService.deleteRating(userId, movieId);
    }
}

