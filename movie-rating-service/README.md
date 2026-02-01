# Movie Rating Service

A Spring Boot backend for a simplified movie rating system.  
It provides public movie browsing, authenticated rating management, and top-rated listings.

## Tech Stack
- Java 21, Spring Boot 3
- Spring Web, Spring Security (JWT)
- Spring Data JPA + Liquibase
- PostgreSQL
- Caffeine cache
- Micrometer + Prometheus
- Grafana + Loki (logs)
- Testcontainers (integration tests)

## Design Patterns Used
- **Repository Pattern**: `MovieRepository`, `RatingRepository`, `UserRepository` encapsulate DB access.
- **Service Layer Pattern**: `MovieService`, `RatingService`, `AuthService` hold business logic.
- **Strategy Pattern**: `RankingStrategy` selects the ranking rule (average vs most-rated).
- **DTO Pattern**: Request/response objects separate API contracts from entities.
- **Cache-Aside Pattern**: `@Cacheable` for top-rated queries, invalidated on rating changes.
- **Observer/Event Listener**: `RatingChangedEvent` triggers cache invalidation.
- **Filter Pattern**: `JwtAuthenticationFilter`, `RequestLoggingFilter`.
- **Outbox (future)**: In a multi-service setup, an Outbox pattern would be added for reliable cross-service event delivery.

## API Endpoints

### Public
- **GET** `/api/v1/movies`  
  List all movies (paged).

- **GET** `/api/v1/movies/top-rated?strategy=AVERAGE&page=0&size=20`  
  List top-rated movies (paged).  
  `strategy`: `AVERAGE` or `MOST_RATED`.

- **GET** `/api/v1/movies/top-rated/one?strategy=AVERAGE`  
  Get a single top-rated movie.

### Auth
- **POST** `/api/v1/auth/register`  
  Register a user.  
  Body: `{ "email": "...", "password": "..." }`

- **POST** `/api/v1/auth/login`  
  Login and receive JWT.  
  Body: `{ "email": "...", "password": "..." }`

### Ratings (JWT required)
- **PUT** `/api/v1/ratings/{movieId}`  
  Add or update current user's rating for a movie.  
  Body: `{ "value": 1..10 }`

- **DELETE** `/api/v1/ratings/{movieId}`  
  Delete current user's rating for a movie.

- **GET** `/api/v1/ratings/{movieId}`  
  Get current user's rating for a specific movie.

- **GET** `/api/v1/ratings/my?page=0&size=20&sort=updatedAt,desc`  
  List current user's ratings (paged).

## Error Handling
Errors follow RFC 7807 `ProblemDetail` with custom properties:
`code`, `details`, `path`, `timestamp`.

## Metrics and Logs
- Prometheus metrics: `GET /actuator/prometheus`
- Grafana + Loki for logs (via Docker Compose)
- Request logging: method, path, status, duration

## Running Locally
### With Docker Compose
```
docker compose up --build
```
Grafana: `http://localhost:3000` (admin/admin)  
Prometheus: `http://localhost:9090`

### With Gradle
```
./gradlew bootRun
```

## Tests
```
./gradlew test
```

## JWT Config
Environment variables:
- `JWT_SECRET` (HMAC secret)
- `security.jwt.issuer` (default: `movie-rating-service`)
- `security.jwt.ttl` (default: `PT30M`)
