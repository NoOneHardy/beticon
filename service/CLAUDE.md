# CLAUDE.md — Service

Spring Boot 4.2 backend on Java 25. Spring Data JPA (MySQL), Spring Security with JWT (JJWT), MapStruct, Lombok, Angus Mail.

## Commands

Run from the repo root:

```bash
./gradlew :service:build
./gradlew :service:lint      # Spotless with the Eclipse formatter (config/eclipse-formatter.xml); fix with :service:spotlessApply
./gradlew :service:test      # JUnit 5; @SpringBootTest + embedded H2 for integration tests, @WebMvcTest slice (controller/ApiTestBase) for API tests
./gradlew :service:bootRun   # http://localhost:26001
```

## Configuration

`application.yml` reads its settings from environment variables: `DATABASE_HOST`/`DATABASE_USER`/`DATABASE_PW`, `SECRET_KEY` (JWT), `SMTP_*`, and the optional `BASE_URL` (frontend URL used in email links), `JOKER_DOUBLE`, `JOKER_TRIPLE`. `bootRun` loads `service/.env` (copy it from `.env.example`) into its environment through the root `build.gradle.kts`. That parser keeps everything after the `=`, so the inline `# …` comments from `.env.example` have to be deleted, or they end up in the values. The `local` profile (`--args='--spring.profiles.active=local'`) points email links at http://localhost:26000 and fixes the Joker limits.

## Where things live

- Exceptions are mapped to `ApiError` in `GlobalExceptionHandler` (exception/); `ApiResponseAdvice` (config/) wraps successful responses in the `BaseResponse` envelope.
- Authentication: `JwtAuthenticationFilter` validates tokens.
- Verification and password-reset emails: `MailService` (service/).
- Helper classes: `DateHelper`, `StringHelper`, `ListHelper`.
