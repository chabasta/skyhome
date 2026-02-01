package com.sky.movieratingservice.repository.ratings;

import com.sky.movieratingservice.entity.Rating;
import com.sky.movieratingservice.repository.view.TopRatedMovieView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    Optional<Rating> findByUserIdAndMovieId(UUID userId, UUID movieId);

    Page<Rating> findAllByUserId(UUID userId, Pageable pageable);

    @Modifying
    @Query(value = """
            insert into ratings (id, user_id, movie_id, value, created_at, updated_at)
            values (:id, :userId, :movieId, :value, :now, :now)
            on conflict (user_id, movie_id)
            do update set value = excluded.value, updated_at = excluded.updated_at
            """, nativeQuery = true)
    int upsert(
            @Param("id") UUID id,
            @Param("userId") UUID userId,
            @Param("movieId") UUID movieId,
            @Param("value") short value,
            @Param("now") Instant now
    );

    @Query(value = """
            select rating.movie.id as movieId,
                   rating.movie.name as movieName,
                   avg(rating.value) as avgRating,
                   count(rating.id) as ratingsCount
            from Rating rating
            group by rating.movie.id, rating.movie.name
            order by avg(rating.value) desc, count(rating.id) desc
            """,
            countQuery = """
            select count(distinct rating.movie.id)
            from Rating rating
            """)
    Page<TopRatedMovieView> findTopRated(Pageable pageable);

    @Query(value = """
            select rating.movie.id as movieId,
                   rating.movie.name as movieName,
                   avg(rating.value) as avgRating,
                   count(rating.id) as ratingsCount
            from Rating rating
            group by rating.movie.id, rating.movie.name
            order by count(rating.id) desc, avg(rating.value) desc
            """,
            countQuery = """
            select count(distinct rating.movie.id)
            from Rating rating
            """)
    Page<TopRatedMovieView> findMostRated(Pageable pageable);
}
