# Flyway vs Liquibase on Boot 4.2

Research for #495 (parent map #493). Question: which schema migration tool fits the service (Spring Boot 4.2, MySQL in production, embedded H2 in tests, schema so far managed by `ddl-auto: update`)? Covers Boot 4 starter support, H2/MySQL pitfalls, baselining the existing production schema, and what `ddl-auto: validate` catches.

Researched 2026-11-27 against `513f53a`.

## Answer

**Flyway**, with plain MySQL SQL migrations, a V1 baseline script taken from a `mysqldump --no-data` of production, `spring.flyway.baseline-on-migrate` for the one-time adoption, `ddl-auto: validate` everywhere, and H2 switched to `MODE=MySQL;DATABASE_TO_LOWER=TRUE` in tests.

Why:

- Both tools are equally supported by Boot 4.2 (one starter each, same auto-configuration model), so support does not decide it.
- The schema is small (9 tables) and MySQL-only in production; the changes the restructure needs are renames and moves, which are easiest to review as plain SQL. Flyway is SQL-first; Liquibase's main advantage, database-agnostic changelogs, mostly matters for running the same changes on H2 and MySQL, and the prototype below shows H2 in MySQL mode already runs the production DDL unchanged.
- Flyway's baseline is a built-in, Boot-configurable property; Liquibase's (`changelog-sync`) is a CLI command Boot does not run, so it needs a manual step against production or preconditions on every baseline changeset.
- Licensing: Flyway Community is Apache 2.0 ([GitHub](https://github.com/flyway/flyway)). Liquibase Community 5.0+ (Boot 4.2 manages 5.0.4) is under the Functional Source License, which Liquibase itself says is not OSI open source; internal use to migrate your own database is allowed ([Liquibase blog](https://www.liquibase.com/blog/liquibase-community-for-the-future-fsl)). Not a blocker, but a point against.

Liquibase stays a reasonable choice if the project later wants rollback scripts or truly database-agnostic changesets; nothing below makes it unworkable.

## Versions in play

Resolved from `./gradlew :service:dependencies` and the [Boot 4.2.0 dependency coordinates](https://docs.spring.io/spring-boot/appendix/dependency-versions/coordinates.html):

| Library | Version |
|---|---|
| Spring Boot | 4.2.0 (`gradle/libs.versions.toml`) |
| Hibernate ORM | 7.4.15.Final |
| H2 | 2.5.252 |
| MySQL Connector/J | 26.10.0 |
| MySQL server | 8.3.0 (`docker-compose.yaml`; assumed to match production) |
| Flyway (`flyway-core`, `flyway-mysql`) | 12.11.0 (managed) |
| Liquibase (`liquibase-core`) | 5.0.4 (managed) |

The [Boot 4.2 release notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.2-Release-Notes) list the Flyway 12.11.0 and H2 2.5.252 upgrades and nothing else about either tool or database initialization. Flyway 12's [breaking changes](https://github.com/flyway/flyway/releases/tag/flyway-12.0.0) (config namespaces, code analysis, MongoDB, `CommandExtension`) do not touch a Boot + MySQL setup.

## Boot 4 starters and auto-configuration

- Boot 4 split auto-configuration into modules. The [4.0 migration guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide) says that where you used to add only the third-party dependency, you now "need to replace that with `spring-boot-starter-flyway` or `spring-boot-starter-liquibase`". The modules are `spring-boot-flyway` / `spring-boot-liquibase`; test starters `spring-boot-starter-flyway-test` / `spring-boot-starter-liquibase-test` exist. Adding `flyway-core` alone, as most pre-Boot-4 tutorials show, will not run migrations.
- Flyway additionally needs a database module for MySQL: "use ... `org.flywaydb:flyway-mysql` with MySQL"; the starter alone covers in-memory and file-based databases (H2) only ([Boot how-to: Database Initialization](https://docs.spring.io/spring-boot/how-to/data-initialization.html)).
- Defaults: Flyway reads `classpath:db/migration` (`V<VERSION>__<NAME>.sql`), with an optional `{vendor}` placeholder (`db/migration/{vendor}` resolves to `db/migration/mysql`). Liquibase reads `db/changelog/db.changelog-master.yaml`. Both run on startup and before tests (same page).
- Test-only data: Flyway runs `src/test/resources/db/migration/V9999__*.sql` only in tests; Liquibase uses contexts (same page).
- The how-to recommends one schema mechanism only: "If you are using a higher-level database migration tool, like Flyway or Liquibase, you should use them alone to create and initialize the schema."

### `ddl-auto` defaults change when a migration tool is added

The same how-to: "If an embedded database is identified and no schema manager (Flyway or Liquibase) has been detected, `ddl-auto` defaults to `create-drop`. In all other cases, it defaults to `none`."

Concretely for this repo:

- `service/src/test/resources/application.yml` sets no `ddl-auto`, so tests currently get `create-drop` on H2 by default. Adding either starter silently turns that into `none`, and the migrations become the only thing creating the test schema.
- `service/src/main/resources/application.yml` sets `ddl-auto: update` explicitly. That has to be changed to `validate` in the same change that adds the tool, or Hibernate keeps altering the schema behind the migrations.

## The current schema

Entities live under `service/src/main/java/ch/no1hardy/service/model/`. Two tables and one column have explicit names with hyphens (`@Table(name = "beticon-user")`, `@Table(name = "beticon-group")`, `@Column(name = "sort-order")`); the other seven tables (`bet`, `game`, `score`, `team`, `notification`, `notification_preference`, `verification_code`) are derived from class names through Boot's snake-case naming strategy. Moving or renaming an entity class during the restructure therefore renames its table under `ddl-auto: update`, which creates a new empty table and leaves the old one behind. This is the main reason the map wants a migration tool first.

Hibernate quotes the hyphenated names with backticks in the MySQL DDL, so every hand-written migration must quote them too (`` `beticon-user` ``).

### Production has drifted from what Hibernate would create today

Two observations, both made locally (production not touched):

1. Fresh DDL that Hibernate 7.4 generates for MySQL (via `jakarta.persistence.schema-generation.scripts.action=create` with `MySQLDialect`): ordinal enum columns are plain `tinyint` with **no** check constraint; `Notification.type` (`@Enumerated(STRING)`) is a native `enum('APPROVAL',...,'REMINDER')` column.
2. `mysqldump --no-data` of the local dev database (`beticon-dev-mysql`, MySQL 8.3.0, schema built over time by `ddl-auto: update`) has the same tables and columns, **plus** check constraints an earlier Hibernate version created and `update` never removes:
   - `` `beticon-user` ``: `CHECK (role between 0 and 1)`, `CHECK (user_application_status between 0 and 2)`
   - `notification_preference`: `CHECK (channel between 0 and 1)`, `CHECK (type between 0 and 5)`
   - `verification_code`: `CHECK (type between 0 and 1)`

   Production was built the same way, so it very likely carries the same constraints. Consequences: adding a constant to `NotificationType` (ordinal 6) would make inserts into `notification_preference` fail on production while the H2 test suite (fresh schema, no checks) passes; adding a value to the `notification.type` native enum needs an `ALTER TABLE ... MODIFY` that `update` does not issue. With a migration tool these become explicit migrations instead of production surprises.

Also visible in the dump: there are no unique constraints on `username` or `email`; uniqueness is enforced in code only. Not a migration-tool question, but the baseline captures it as-is.

**So the V1 baseline script must come from a schema-only dump of production, not from Hibernate's generated DDL.** The two differ, and only the dump describes what production actually has.

## Baselining the existing production schema

### Flyway

- `baselineOnMigrate`: "Whether to automatically call baseline when migrate is executed against a non-empty schema with no schema history table." Default `false`. Warning: "Be careful when enabling this as it removes the safety net that ensures Flyway does not migrate the wrong database in case of a configuration mistake!" "Only migrations above baselineVersion will then be applied." ([setting reference](https://documentation.red-gate.com/fd/flyway-baseline-on-migrate-setting-277578974.html))
- `baselineVersion`: "The version to tag an existing schema with when executing baseline." Default `1`. ([setting reference](https://documentation.red-gate.com/fd/flyway-baseline-version-setting-277578975.html))
- Without baselining, Flyway refuses to migrate a non-empty schema that has no history table; "Flyway will not baseline over an existing schema history table" ([Baselines](https://documentation.red-gate.com/fd/baselines-273973441.html)).
- Boot exposes these as `spring.flyway.baseline-on-migrate` and `spring.flyway.baseline-version` ([`FlywayProperties`](https://docs.spring.io/spring-boot/api/java/org/springframework/boot/flyway/autoconfigure/FlywayProperties.html)).

Concrete sequence:

1. `mysqldump --no-data --skip-comments` of production; turn it into `db/migration/V1__baseline.sql`. Drop the `/*!...*/` and `SET` lines, and move the `CONSTRAINT FK... FOREIGN KEY` clauses out into trailing `ALTER TABLE ... ADD CONSTRAINT` statements: the dump orders tables alphabetically and relies on `FOREIGN_KEY_CHECKS=0` (inside the stripped comments), so `bet` references `` `beticon-user` `` before it exists. H2 rejected the raw dump for exactly that reason (`Table "beticon-user" not found`).
2. First deploy with `spring.flyway.baseline-on-migrate=true` and `baseline-version=1` (the default). On production, Flyway finds a non-empty schema without `flyway_schema_history`, records baseline `1`, and skips `V1`. On an empty database (H2 tests, a fresh dev volume) there is nothing to baseline, so `V1` runs and creates the schema.
3. `V2+` hold the real changes (for example renames from the restructure).
4. Given the warning above, consider removing `baseline-on-migrate` after production has been baselined once, so a misconfigured URL cannot silently baseline a wrong database. The dev database (`beticon-dev-mysql`) gets baselined the same way.

MySQL DDL is not transactional (implicit commit), so a migration that fails halfway leaves a partial change and a failed row in the history table; keep migrations small (one DDL concern each). This applies to Liquibase equally.

### Liquibase

The official route ([existing database](https://docs.liquibase.com/community/implementation-guide-5-0/generate-changelog-from-existing-database), [existing project](https://www.liquibase.org/documentation/existing_project.html)) is `generate-changelog` to capture the schema, then `changelog-sync` to fill `DATABASECHANGELOG` "as if you have been using Liquibase all along" without changing the schema. `changelog-sync` is a CLI command; Boot's integration only runs `update` and has no baseline property, so this is a manual run against production before the first deploy, or each baseline changeset gets a `tableExists` precondition with `onFail: MARK_RAN`. Both work, both are more moving parts than one Flyway property.

## H2 vs MySQL in tests

Tested directly with the H2 2.5.252 jar from the Gradle cache:

| DDL from the MySQL dump | H2 default mode | H2 `MODE=MySQL;DATABASE_TO_LOWER=TRUE` |
|---|---|---|
| Backtick-quoted `` `beticon-user` `` | accepted | accepted |
| `datetime(6)` | **rejected** (`Unknown data type: "DATETIME"`) | accepted |
| `ENGINE=InnoDB DEFAULT CHARSET=... COLLATE=...` | **rejected** (syntax error) | accepted |
| `enum(...)`, `bit(1)`, `tinyint`, `CHECK (...)` | not reached | accepted |
| FK to a table created later | rejected | rejected (reorder needed, see above) |

The URL form comes from [H2's MySQL compatibility mode](https://www.h2database.com/html/features.html) (`jdbc:h2:~/test;MODE=MySQL;DATABASE_TO_LOWER=TRUE`), which also warns that "only a small subset of the differences between databases are implemented" and that text comparison stays case-sensitive unless `IGNORECASE` is set. Expect future hand-written MySQL migrations (`ALTER TABLE ... RENAME COLUMN`, `MODIFY`) to need a check against H2 too; H2 says some MySQL `ALTER TABLE` forms are only "partially supported".

### End-to-end prototype (reverted, not on this branch)

Added `spring-boot-starter-flyway` + `flyway-mysql`, the reordered dump as `V1__baseline.sql`, and in the test `application.yml` the MySQL-mode URL plus `ddl-auto: validate` (dialect left as `H2Dialect`). Results of `./gradlew :service:test`:

- Flyway created the schema on H2 and Hibernate `validate` passed: the context started and **305 of 337 tests passed**.
- The 32 failures were all in `UserServiceTest` (`DataIntegrityViolationException`, FK `bet.user_id`), and the class passes when run alone. Cause: test isolation today relies on `create-drop` rebuilding the schema whenever a new Spring test context starts. With Flyway the in-memory `testdb` is migrated once and keeps rows across contexts, so bets left by another test class block `UserServiceTest`'s `repository.deleteAll()`. The migration ticket must replace that implicit reset, for example with a test `FlywayMigrationStrategy` that calls `clean()` then `migrate()` (needs `spring.flyway.clean-disabled=false` in tests only), a unique H2 database name per context, or tests that clean up after themselves.

Also checked: `ddl-auto: validate` with `MySQLDialect` against the local dev MySQL 8.3 schema (the one built by `update`) passes. So switching production to `validate` should not fail on startup as long as production matches dev.

## What `ddl-auto: validate` catches

From the Hibernate 7.4 source ([`AbstractSchemaValidator`](https://github.com/hibernate/hibernate-orm/blob/7.4/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/AbstractSchemaValidator.java), [`ColumnDefinitions`](https://github.com/hibernate/hibernate-orm/blob/7.4/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/ColumnDefinitions.java), [`ConstraintValidationType`](https://github.com/hibernate/hibernate-orm/blob/7.4/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/ConstraintValidationType.java), [`SchemaToolingSettings`](https://github.com/hibernate/hibernate-orm/blob/7.4/hibernate-core/src/main/java/org/hibernate/cfg/SchemaToolingSettings.java)); startup fails with `SchemaManagementException` on:

- **Missing table**: "Schema validation: missing table [...]". This is the one that matters for the restructure: an entity moved or renamed so its derived table name changed, without a matching `RENAME TABLE` migration, stops the app instead of creating an empty table.
- **Missing column**: "Schema validation: missing column [...] in table [...]" (field renamed without a migration).
- **Wrong column type**: compared by JDBC type code or normalized type name only (`dialect.equivalentTypes(...)`, arguments stripped).
- **Missing sequence** / inconsistent increment size (none mapped here; ids are `GenerationType.UUID`).
- **Indexes and unique keys**, only when opted in: `hibernate.tooling.schema.index_validation` and `hibernate.tooling.schema.unique_key_validation` (since 7.3, values `NONE`/`NAMED`/`ALL`, default `NONE`).

It does **not** catch: extra tables or columns left in the database, length/precision differences, nullability, foreign keys, check constraints, or the value list of a native `enum(...)` column. The check-constraint drift above is invisible to `validate`, which is why it has to be fixed by an explicit migration rather than discovered at startup.

## Suggested shape for the migration-tool ticket

Not a decision, just what the findings imply:

1. Add `spring-boot-starter-flyway` and `runtimeOnly("org.flywaydb:flyway-mysql")`.
2. `V1__baseline.sql` from a production schema dump, FKs moved to the end.
3. `spring.flyway.baseline-on-migrate=true` for the first production deploy; `ddl-auto: validate` in `application.yml`.
4. Tests: `MODE=MySQL;DATABASE_TO_LOWER=TRUE`, explicit `ddl-auto: validate`, and a replacement for the per-context schema reset.
5. Separately decide whether a `V2` drops the stale ordinal check constraints (or keeps them and makes enum additions update them).
