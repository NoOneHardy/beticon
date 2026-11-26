# Coding Standards

Rules reviewers (human or agent) check changes against. Subproject conventions in `service/CLAUDE.md` and `frontend/CLAUDE.md` apply as well.

## Comments

- A comment says only what the code, the `GLOSSARY.md` or graphify can't: a non-obvious reason, a gotcha, a link to an issue. Purpose and `@return` comments that repeat the name or signature are removed, including ones already in files a change touches.
- A comment sits on the code it explains, never on another method's behaviour.
- If a comment is needed to tell two methods apart, rename them.

## Service (Java)

### DTOs

- DTOs carry data only. Validation that is business logic lives in a service or the domain.
- A DTO value that may be missing is `Optional<T>`, so the caller sees it can be absent and doesn't null-check by hand.

### Records for immutable objects

- An object that does not change after it is built is a Java `record`, not a Lombok `@Data` class: request and response DTOs, value objects, JPA embeddables (e.g. `Scoreline`), error payloads.
- Classes stay classes only where they must be mutable or extend something: JPA entities, Spring beans, exceptions.
- Defaults (an empty list, `false`, `0`) go in the record's compact constructor.
- A record cannot extend another record. Reuse one by composition; for a response DTO that has to stay flat in JSON, annotate the composed component with `@JsonUnwrapped` (see `ExtendedTeamRes`).
- Related DTOs use the same field names for the same thing.

### No builders on records

- Records have no `@Builder` and no `toBuilder()`. Build them with the canonical constructor, in production code and in tests.
- A class keeps a builder only while something uses it; an unused builder is removed.

### Derived values, not mutation

- A request is never changed in place (no `clamp()`-style methods). Derived values are accessors that return a new value, e.g. `ScoreReq.scoreline()`, `BetReq.normalizedJoker()`.
- Name an accessor after what it does. A method that only fills in a missing value is not "clamped".
- A derived accessor does not throw on input that validation rejects anyway; it returns an empty `Optional` for a missing value.

### Domain types over loose values

- Values that always travel together become one type (the home and guest goals are a `Scoreline`), and the rule that belongs to them lives on that type, not repeated at each call site.
- Scoring rules live in the Scoring module (`model/game/Scoring`, `Scoreline`, `PointsBreakdown`) and nowhere else.

### Refactors stay refactors

- A refactor does not change behaviour beyond what the refactor itself forces. If it has to, the change is named in the PR and covered by a test.
- A behaviour change that is a decision of its own (e.g. a new validation rule) gets its own issue, not a ride inside a refactor.
- The JSON contract of the API does not change in a refactor; check it with a serialization test when a DTO's shape is touched.

### Tests

- Tests are written first, integration tests included. Each new test is seen failing for the reason it claims before it is made green.
