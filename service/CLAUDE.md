# CLAUDE.md — Service

Spring Boot 4.2 backend on Java 25. Spring Data JPA (MySQL), Spring Security with JWT (JJWT), MapStruct, Lombok, Angus Mail.

## Commands

Run from the repo root:

```bash
./gradlew :service:build
./gradlew :service:lint      # Spotless with the Eclipse formatter (config/eclipse-formatter.xml); fix with :service:spotlessApply
./gradlew :service:test      # JUnit 5; @SpringBootTest + embedded H2 for integration tests, @WebMvcTest slice (controller/ApiTestBase) for API tests
./gradlew :service:bootRun   # http://localhost:8080
```

## Configuration

Requires a `.env` file (copy from `.env.example`); values override `application.properties`. Key settings: `jwt.secret`, database credentials, SMTP settings, `base.url` (frontend URL used in email links).

## Where things live

- Exceptions are mapped to `ApiError` in `GlobalExceptionHandler` (exception/); `ApiResponseAdvice` (config/) wraps successful responses in the `BaseResponse` envelope.
- Authentication: `JwtAuthenticationFilter` validates tokens.
- Verification and password-reset emails: `MailService` (service/).
- Helper classes: `DateHelper`, `StringHelper`, `ListHelper`.
