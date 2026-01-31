package com.sky.movieratingservice.api.dto;

import java.time.Instant;
import java.util.UUID;

public record RatingResponse(
        UUID id,
        UUID userId,
        UUID movieId,
        short value,
        Instant createdAt,
        Instant updatedAt
) {
}
