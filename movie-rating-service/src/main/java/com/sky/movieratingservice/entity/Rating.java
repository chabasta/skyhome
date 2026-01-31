package com.sky.movieratingservice.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ratings"
)
public class Rating {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private short value;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Rating() {}

    public Rating(UUID id, User user, Movie movie, short value, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.user = user;
        this.movie = movie;
        this.value = value;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public Movie getMovie() { return movie; }
    public short getValue() { return value; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setValue(short value) {
        this.value = value;
        this.updatedAt = Instant.now();
    }
}
