package com.sky.movieratingservice.repository.movies;

import com.sky.movieratingservice.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {

}

