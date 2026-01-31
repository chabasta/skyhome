package com.sky.movieratingservice.events;

import java.util.UUID;

public record RatingChangedEvent(UUID movieId) {
}
