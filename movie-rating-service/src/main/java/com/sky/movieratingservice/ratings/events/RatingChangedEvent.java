package com.sky.movieratingservice.ratings.events;

import java.util.UUID;

public record RatingChangedEvent(UUID movieId) {
}

