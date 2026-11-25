# CLAUDE.md — Service

Spring Boot 4.2 backend on Java 25. Spring Data JPA (MySQL), Spring Security with JWT (JJWT), MapStruct, Lombok, Angus Mail.

## Commands

Run from the repo root:

```bash
./gradlew :service:build
./gradlew :service:test      # JUnit 5; @SpringBootTest + embedded H2 for integration tests, @WebMvcTest slice (controller/ApiTestBase) for API tests
./gradlew :service:bootRun   # http://localhost:8080
```

## Configuration

Requires a `.env` file (copy from `.env.example`); values override `application.properties`. Key settings: `jwt.secret`, database credentials, SMTP settings, `base.url` (frontend URL used in email links).

## Conventions

- One controller per domain (GameController, UserController, AdminController, …); keep business logic in service classes, not controllers.
- Always expose DTOs in API contracts, mapped from entities via MapStruct.
- Exceptions are mapped to `ApiError` in `GlobalExceptionHandler` (exception/); `ApiResponseAdvice` (config/) wraps successful responses in the `BaseResponse` envelope.
- Authentication: `JwtAuthenticationFilter` validates tokens.
- Emails (verification/reset) are sent asynchronously.
- Scoring rules (point values, Joker multiplication, winner/tie/total checks) live in the Scoring module in the game model (`model/game/Scoring`, `Scoreline`, `PointsBreakdown`); never re-implement them elsewhere.
- Helper classes: `DateHelper`, `StringHelper`, `ListHelper`.
