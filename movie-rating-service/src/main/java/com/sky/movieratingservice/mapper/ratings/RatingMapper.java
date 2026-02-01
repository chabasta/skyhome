package com.sky.movieratingservice.mapper.ratings;

import com.sky.movieratingservice.api.dto.RatingResponse;
import com.sky.movieratingservice.entity.Rating;
import org.springframework.stereotype.Component;

@Component
public class RatingMapper {

    public RatingResponse toResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getUser().getId(),
                rating.getMovie().getId(),
                rating.getValue(),
                rating.getCreatedAt(),
                rating.getUpdatedAt()
        );
    }
}

