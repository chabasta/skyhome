--liquibase formatted sql

--changeset sky:003-seed-movies
insert into movies (id, name, created_at)
values ('11111111-1111-1111-1111-111111111111', 'The Shawshank Redemption', now()),
       ('22222222-2222-2222-2222-222222222222', 'The Godfather', now()),
       ('33333333-3333-3333-3333-333333333333', 'The Dark Knight', now());