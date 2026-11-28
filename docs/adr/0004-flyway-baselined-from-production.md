# Flyway owns the schema, baselined from the production schema

Flyway migrations replace `ddl-auto: update`, and Hibernate runs with `ddl-auto: validate` in every environment, tests included, so a service whose entities no longer match the database refuses to start instead of silently creating empty tables. V1 is a schema-only `mysqldump` of production, not the DDL Hibernate generates: databases built by `update` carry stale CHECK constraints (for example `notification_preference.type between 0 and 5`) that current Hibernate no longer emits and `update` never removed, so generated DDL would give fresh databases a different schema than production under the same version. Existing databases (production, beticon-dev) are not migrated to V1 but baselined at it: `spring.flyway.baseline-on-migrate` stays on permanently, so each one records V1 as applied on its first start and only receives V2 onward.

## Considered Options

- **Liquibase**: equally supported by Boot, but FSL-licensed since 5.x, and its changelog abstraction buys nothing for a MySQL-only schema with a handful of tables.
- **Hibernate-generated DDL as V1**: rejected for the drift above.
- **Baselining by hand, or enabling the flag for one release only**: an extra manual step or a removal someone has to remember, for little gain, since the flag is inert once the history table exists.

## Consequences

- Developers' local databases are recreated from V1 rather than baselined, because `update` may have drifted them from production.
- Tests run the same migrations on H2 in `MODE=MySQL;DATABASE_TO_LOWER=TRUE`, with a fresh in-memory database per Spring context to keep the isolation `create-drop` used to give.
- Every entity names its table explicitly, so renaming or moving a class never changes the schema.
- Fixing the stale constraints is a migration of its own, not part of V1.
