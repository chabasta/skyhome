package com.sky.movieratingservice.api.dto;

import java.time.Instant;
import java.util.UUID;

public record MovieResponse(
        UUID id,
        String name,
        Instant createdAt
) {
}

