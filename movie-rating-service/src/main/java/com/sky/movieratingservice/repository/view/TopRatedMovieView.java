package com.sky.movieratingservice.repository.view;

import java.util.UUID;

public interface TopRatedMovieView {
    UUID getMovieId();
    String getMovieName();
    Double getAvgRating();
    Long getRatingsCount();
}

