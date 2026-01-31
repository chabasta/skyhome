--liquibase formatted sql

--changeset sky:002-constraints-and-indexes

-- USERS
create unique index uq_users_email on users (email);

alter table users
    add constraint chk_users_email_not_blank
        check (length(trim(email)) > 0);

-- MOVIES
alter table movies
    add constraint chk_movies_name_not_blank
        check (length(trim(name)) > 0);

create index ix_movies_name on movies (name);

-- RATINGS
alter table ratings
    add constraint chk_ratings_value_range
        check (value between 1 and 10);

create index ix_ratings_movie_id on ratings (movie_id);