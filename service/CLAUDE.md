# CLAUDE.md — Service

Spring Boot 4.2 backend on Java 25. Spring Data JPA (MySQL), Spring Security with JWT (JJWT), MapStruct, Lombok, Angus Mail.

## Commands

Run from the repo root:

```bash
./gradlew :service:build
./gradlew :service:test      # JUnit 5; @SpringBootTest for integration tests, embedded H2 instead of MySQL
./gradlew :service:bootRun   # http://localhost:8080
```

## Configuration

Requires a `.env` file (copy from `.env.example`); values override `application.properties`. Key settings: `jwt.secret`, database credentials, SMTP settings, `base.url` (frontend URL used in email links).

## Conventions

- One controller per domain (GameController, UserController, AdminController, …); keep business logic in service classes, not controllers.
- Always expose DTOs in API contracts, mapped from entities via MapStruct.
- Errors are standardized by the global `ApiResponseAdvice`.
- Authentication: `JwtAuthenticationFilter` validates tokens.
- Emails (verification/reset) are sent asynchronously.
- Helper classes: `DateHelper`, `StringHelper`, `ListHelper`.
