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
- **Strategy Pattern**: `RankingStrategy` + `TopRatedCalculatorRegistry` select ranking rules (average vs most-rated).
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
  Get a single top-rated movie (top-1).

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
- Metrics collected: standard Spring/JVM + HTTP server requests (`http.server.requests`)

### Quick Checks
1) Prometheus targets: `http://localhost:9090/targets` (service should be **UP**)
2) Metrics endpoint: `http://localhost:8080/actuator/prometheus`
3) Grafana: `http://localhost:3000` (admin/admin)
4) Loki logs: Explore → datasource **Loki** → `{container="movie-rating-service"}`

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
Test coverage overview:
- Unit: ranking strategy calculators (pure Mockito)
- Integration: repositories + services with Testcontainers (Postgres + Liquibase)
- Web slice: controllers via MockMvc

## JWT Config
Environment variables:
- `JWT_SECRET` (HMAC secret)
- `security.jwt.issuer` (default: `movie-rating-service`)
- `security.jwt.ttl` (default: `PT30M`)

### Future Improvements
- Add refresh tokens for long-lived sessions.
- Introduce domain-specific exceptions with stable error codes as the API grows.
