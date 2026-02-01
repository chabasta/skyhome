package com.sky.movieratingservice.api.dto;

import java.time.Instant;
import java.util.UUID;

public record RatingSummaryResponse(
        UUID movieId,
        short value,
        Instant createdAt,
        Instant updatedAt
) {
}
