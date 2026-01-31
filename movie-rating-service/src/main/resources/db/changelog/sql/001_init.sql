--liquibase formatted sql

--changeset sky:001-init

create table users
(
    id            uuid primary key,
    email         varchar(254) not null,
    password_hash varchar(255) not null,
    created_at    timestamptz  not null
);

create table movies
(
    id         uuid primary key,
    name       varchar(255) not null,
    created_at timestamptz  not null
);

create table ratings
(
    id         uuid primary key,
    user_id    uuid        not null references users (id) on delete cascade,
    movie_id   uuid        not null references movies (id) on delete cascade,
    value      smallint    not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create unique index uq_ratings_user_movie on ratings (user_id, movie_id);
