package com.sky.movieratingservice.api.controller.ratings;

import com.sky.movieratingservice.api.dto.RatingRequest;
import com.sky.movieratingservice.api.dto.RatingResponse;
import com.sky.movieratingservice.api.dto.RatingSummaryResponse;
import com.sky.movieratingservice.security.CurrentUser;
import com.sky.movieratingservice.service.ratings.RatingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final CurrentUser currentUser;

    public RatingController(RatingService ratingService, CurrentUser currentUser) {
        this.ratingService = ratingService;
        this.currentUser = currentUser;
    }

    @PutMapping("/{movieId}")
    public RatingResponse addOrUpdate(
            @PathVariable UUID movieId,
            @Valid @RequestBody RatingRequest request
    ) {
        return ratingService.addOrUpdateRating(currentUser.requireUserId(), movieId, request.value());
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID movieId
    ) {
        ratingService.deleteRating(currentUser.requireUserId(), movieId);
    }

    @GetMapping("/{movieId}")
    public RatingSummaryResponse getMyRating(
            @PathVariable UUID movieId
    ) {
        return ratingService.getMyRating(currentUser.requireUserId(), movieId);
    }

    @GetMapping("/my")
    public Page<RatingSummaryResponse> listMyRatings(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ratingService.listMyRatings(currentUser.requireUserId(), pageable);
    }
}
