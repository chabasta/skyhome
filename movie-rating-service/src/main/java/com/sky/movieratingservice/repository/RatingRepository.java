package com.sky.movieratingservice.repository;

import com.sky.movieratingservice.entity.Rating;
import com.sky.movieratingservice.repository.view.TopRatedMovieView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByUserIdAndMovieId(UUID userId, UUID movieId);

    @Query("""
            select r.movie.id as movieId,
                   r.movie.name as movieName,
                   avg(r.value) as avgRating,
                   count(r.id) as ratingsCount
            from Rating r
            group by r.movie.id, r.movie.name
            order by avg(r.value) desc, count(r.id) desc
            """)
    Optional<TopRatedMovieView> findTopRated();
}
