# Spring Modulith and ArchUnit on Boot 4.2

Research for #494 (parent map #493, "Service structure by domain"). Researched 2026-11-27 against primary sources: the Spring Modulith 2.2.0 tag and reference docs, ArchUnit's GitHub releases and user guide, and the Spring Boot 4.2.0 BOM.

## Short answer

- **Spring Modulith 2.2.0 (GA, 2026-11-19) is built against Spring Boot 4.2.0 and Spring Framework 7.1**, so it fits our stack. It compiles to Java 17 bytecode and runs on Java 25. It brings ArchUnit 1.5.1 with it, which reads class files up to Java 27.
- **ArchUnit 1.5.1 (2026-09-25) supports Java 25** (since 1.4.1) and ships `archunit-junit6` (since 1.5.0), which matches the JUnit Jupiter 6.1.3 that Boot 4.2.0 manages.
- **Neither tool has a Gradle plugin.** Both enforce rules as ordinary JUnit tests, so `./gradlew :service:test` (and therefore `:service:build` and CI) fails on a violation. That is the only enforcement point.
- **Modulith** gives verified module boundaries, named interfaces, `allowedDependencies`, cycle detection, generated diagrams and canvases, `@ApplicationModuleTest` module slices, `Scenario` event tests and an optional JPA event publication registry. All of that comes from conventions: package layout and a few annotations.
- **ArchUnit on its own** can express all of the above boundary rules and more, such as layering inside a module or naming rules. But you write every rule yourself, and you get no docs, test slicing or events.
- **They are not alternatives.** Modulith's verification *is* ArchUnit. A Modulith project can add plain ArchUnit rules for whatever Modulith does not cover, using the same dependency it already has.

## Versions in this repo

From `gradle/libs.versions.toml`, `build.gradle.kts`, `service/build.gradle.kts` and `gradle/wrapper/gradle-wrapper.properties`:

| Item | Version |
|---|---|
| Spring Boot (plugin + BOM) | 4.2.0 |
| Java toolchain | 25 |
| Gradle | 9.9.2 |
| JUnit Jupiter (managed by Boot 4.2.0) | 6.1.3 ([BOM](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-dependencies/4.2.0/spring-boot-dependencies-4.2.0.pom), `junit-jupiter.version`) |
| Spring Modulith / ArchUnit | not used today |

The Boot 4.2.0 BOM manages **neither Spring Modulith nor ArchUnit** (no `modulith` or `archunit` entries in `spring-boot-dependencies-4.2.0.pom`). To use them we would import `org.springframework.modulith:spring-modulith-bom` ourselves, or pin ArchUnit in `libs.versions.toml`.

## Compatibility as of 2026-11-27

### Spring Modulith

- Current releases ([GitHub releases](https://github.com/spring-projects/spring-modulith/releases)): **2.2.0** (2026-11-19), 2.1.3, 2.0.10 and 1.4.14 (Boot 3.x line).
- The 2.2.0 release notes say "Upgrade to Spring Boot 4.2.0", "Upgrade to Spring Framework 7.1" and "Upgrade to ArchUnit 1.5.1" ([release 2.2.0](https://github.com/spring-projects/spring-modulith/releases/tag/2.2.0)).
- The root `pom.xml` at tag 2.2.0 sets `spring-boot.version` to `4.2.0`, `archunit.version` to `1.5.1` and `jmolecules-bom.version` to `2025.0.2`, and compiles with `<source>17</source><target>17</target>`. So Java 17 is the baseline, and Java 25 runs it fine ([pom.xml@2.2.0](https://github.com/spring-projects/spring-modulith/blob/2.2.0/pom.xml)).
- **Docs caveat:** the "Spring Boot Compatibility" table in the reference appendix is stale. At tag 2.2.0 its newest row is still "2.0 (snapshot) / 4.0 SNAPSHOT" ([appendix.adoc@2.2.0](https://github.com/spring-projects/spring-modulith/blob/2.2.0/src/docs/antora/modules/ROOT/pages/appendix.adoc)). Trust the release notes and the pom instead.
- Use 2.2.x. The 2.1.x line is built against Boot 4.1 (2.1.3 pins `spring-boot.version` 4.1.3 and ArchUnit 1.4.2).

### ArchUnit

- Latest is **1.5.1** (2026-09-25) ([releases](https://github.com/TNG/ArchUnit/releases)).
- Java 25 (class file major 69) has been supported since 1.4.1 and was backported to 1.3.2. 1.4.2 added Java 26 and 1.5.0 added Java 27 ([v1.4.1](https://github.com/TNG/ArchUnit/releases/tag/v1.4.1), [v1.5.0](https://github.com/TNG/ArchUnit/releases/tag/v1.5.0)).
- 1.5.0 added `archunit-junit6` ("supports ArchUnit with JUnit 6"), and since 1.5.0 ArchUnit itself is built and tested with Gradle 9 and JDK 25 ([v1.5.0](https://github.com/TNG/ArchUnit/releases/tag/v1.5.0)). Because Boot 4.2 is on JUnit 6, use `archunit-junit6` if we want the `@AnalyzeClasses`/`@ArchTest` engine. Plain `archunit` with a normal `@Test` also works.

## What Spring Modulith gives us

### Module model (conventions)

Source: [Fundamentals](https://docs.spring.io/spring-modulith/reference/fundamentals.html).

- **Main package**: the package of the `@SpringBootApplication` class, here `ch.no1hardy.service`. By default, every direct sub-package is one application module.
- **Today, that means our layer packages would be the modules**: `controller`, `service`, `model`, `mapper`, `front`, `config`, `common`, `storage`, `provider` and `exception`. The tool only becomes meaningful after a restructure to `game`, `bet`, `user` and so on.
- **API vs internal**: a module's base package is its API. Its sub-packages (such as `bet.internal`) are internal and must not be referenced from other modules. Public types in internal packages are still rejected by verification, even though the compiler allows the access.
- **Nested modules**: `@ApplicationModule` on a sub-package's `package-info.java`.
- **Detection strategy**: `spring.modulith.detection-strategy` can be `direct-subpackages` (default), `explicitly-annotated` (only packages with `@ApplicationModule` or jMolecules `@Module`), or the class name of a custom `ApplicationModuleDetectionStrategy`.
- **`@ApplicationModule(allowedDependencies = …)`** on `package-info.java` restricts which modules this one may use. The syntax:
  - `"user"`: the base package
  - `"user :: spi"`: one named interface
  - `"user :: *"`: all named interfaces, excluding the base package
  - `"user :: **"`: everything exposed, new in 2.2 ([#1924](https://github.com/spring-projects/spring-modulith/issues/1924))
  - Without `allowedDependencies`, the module is unrestricted (except for internals).
- **`@NamedInterface("name")`** on a sub-package's `package-info.java` exposes that package as a named port. Since 2.2 it can also be declared on types ([#1839](https://github.com/spring-projects/spring-modulith/issues/1839)).
- **`type = Type.OPEN`** marks a module whose internals others may use. The docs present it as a tool for gradual migration of legacy code.
- **`@Modulithic(systemName, sharedModules, additionalPackages)`** on the application class.

### Verified module boundaries

Source: [Verification](https://docs.spring.io/spring-modulith/reference/verification.html).

```java
ApplicationModules.of(ServiceApplication.class).verify();
```

`verify()` throws on any violation of three rules:

1. **No cycles between application modules.** The dependencies must form a directed acyclic graph.
2. **Access to other modules only through API packages.** References into internal packages are rejected, except into OPEN modules.
3. **Only explicitly allowed dependencies** for modules that declare `allowedDependencies`.

Details:

- `detectViolations().filter(…).throwIfPresent()` lets a test filter known violations, for example during a migration.
- `VerificationOptions` adds extra checks. If jMolecules ArchUnit rules are on the classpath, they run automatically (DDD and hexagonal/onion rules).
- **What counts as a dependency.** `DependencyType` at 2.2.0 distinguishes `USES_COMPONENT` (bean injection), `ENTITY` (an entity referring to another module's entity), `EVENT_LISTENER` (listening to another module's event type) and `DEFAULT` (any other type reference) ([DependencyType.java@2.2.0](https://github.com/spring-projects/spring-modulith/blob/2.2.0/spring-modulith-core/src/main/java/org/springframework/modulith/core/DependencyType.java)). All of them count toward the three rules. An event listener therefore *is* a dependency on the publishing module's event type, but not on its beans.
- The model is built by ArchUnit's class-file import (`spring-modulith-core` depends on ArchUnit). The verification test is a plain unit test and needs no Spring context.
- **Runtime verification**: `spring.modulith.runtime.verification-enabled` (default `false`) verifies the module arrangement at application startup. It requires `spring-modulith-runtime` ([appendix](https://github.com/spring-projects/spring-modulith/blob/2.2.0/src/docs/antora/modules/ROOT/pages/appendix.adoc)). It is not needed if the test runs in CI.

### Named interfaces

See the module model above. For #493 this is the mechanism for a narrow cross-module API, for example letting `bet` depend only on `user :: lookup` instead of on all of `user`, which would replace today's `UserService.getRaw*` reach-through.

### Application events and the Event Publication Registry

Source: [Events](https://docs.spring.io/spring-modulith/reference/events.html).

- **`@ApplicationModuleListener`** combines `@Async`, `@Transactional(propagation = REQUIRES_NEW)` and `@TransactionalEventListener`. The listener runs after the publisher commits, in its own transaction, on another thread.
- **Event Publication Registry**: while the publishing transaction is still open, it writes one row per transactional listener, holding the serialized event. The row is completed when the listener succeeds, and failed rows stay behind for retry.
  - Completion modes come from `spring.modulith.events.completion-mode`: `update` (default, keeps rows that must be purged), `delete` or `archive`.
  - Since 2.2, completion by default happens inside the listener transaction (`spring.modulith.events.completion=in-listener`, [#1961](https://github.com/spring-projects/spring-modulith/issues/1961)), and failed and abandoned publications are told apart ([#1764](https://github.com/spring-projects/spring-modulith/issues/1764)).
  - Retry APIs: `IncompleteEventPublications`, `FailedEventPublications` (2.0+), `CompletedEventPublications` and `AbandonedEventPublications` (2.2+). `spring.modulith.events.republish-outstanding-events-on-restart` defaults to `false`.
- **Starters**:
  - `spring-modulith-starter-jpa` uses the JPA entity `DefaultJpaEventPublication`, mapped to table `EVENT_PUBLICATION` ([source](https://github.com/spring-projects/spring-modulith/blob/2.2.0/spring-modulith-events/spring-modulith-events-jpa/src/main/java/org/springframework/modulith/events/jpa/updating/DefaultJpaEventPublication.java)).
  - `spring-modulith-starter-jdbc` creates its table through `spring.modulith.events.jdbc.schema-initialization.enabled` (default `true`) and backs off if Flyway or Liquibase already created it. The MySQL DDL is in appendix D.
  - Both starters serialize events with Jackson through `spring-modulith-events-jackson`.
- **Relevance to #493**: plain Spring `ApplicationEventPublisher` and `@EventListener` work *without* the registry. The registry only adds durability for asynchronous listeners. With the JPA starter, our current `ddl-auto: update` would make Hibernate create `EVENT_PUBLICATION`. But #493 freezes schema changes to "only through a migration tool that also runs on production", so adopting the registry pulls in that migration-tool decision.

### Generated module documentation

Sources: [Documentation](https://docs.spring.io/spring-modulith/reference/documentation.html), [`BuildSystemUtils`@2.2.0](https://github.com/spring-projects/spring-modulith/blob/2.2.0/spring-modulith-docs/src/main/java/org/springframework/modulith/docs/util/BuildSystemUtils.java), [`ApplicationModulesExporter`@2.2.0](https://github.com/spring-projects/spring-modulith/blob/2.2.0/spring-modulith-core/src/main/java/org/springframework/modulith/core/util/ApplicationModulesExporter.java).

- **`new Documenter(modules)`**, from `spring-modulith-docs`, which `spring-modulith-starter-test` includes:
  - `.writeModulesAsPlantUml()`: a C4 component diagram of all modules
  - `.writeIndividualModulesAsPlantUml()`: each module with its direct dependencies
  - `.writeModuleCanvases()`: a per-module Asciidoc table
  - `.writeAggregatingDocument()` and `.writeDocumentation()`: `all-docs.adoc`
  - UML style through `DiagramOptions.withStyle(DiagramStyle.UML)`
- **Canvas contents**: base package, Spring components by stereotype (API and named-interface types only, unless `CanvasOptions.revealInternals()` is set), aggregate roots (entities with repositories), published events (jMolecules `@DomainEvent`), events listened to, and configuration properties (needs `spring-boot-configuration-processor`). `spring-modulith-apt` is an annotation processor that pulls Javadoc into the canvases.
- **Output location**: `BuildSystemUtils` writes to `target/` if a `pom.xml` exists in the working directory, otherwise to `build/`. For a Gradle test run in `service/`, output lands in `service/build/spring-modulith-docs/`. It is generated by running a test, not by a Gradle task.
- **Usable by agents?** Partly:
  - The PlantUML and Asciidoc files are text, so an agent can read them, but they are build output, not committed. To show an agent the module map without running code, we would have to commit the generated files or a hand-written summary, and keep them current.
  - `ApplicationModulesExporter` produces a JSON description of modules, named interfaces, exposed types and dependency types. `toJson()` / `toFullJson()` and a `main(packageName, file)` exist. Its default location `META-INF/spring-modulith/application-modules.json` is used for AOT/runtime. This is the most machine-friendly output, but nothing in the docs presents it as an agent-facing artifact.
  - The most legible module map for an agent is probably the `package-info.java` files themselves: `@ApplicationModule(allowedDependencies = …)` declares each module's allowed dependencies in one line, in source.

### Module slice tests

Source: [Testing](https://docs.spring.io/spring-modulith/reference/testing.html), [`ApplicationModuleTest`@2.2.0](https://github.com/spring-projects/spring-modulith/blob/2.2.0/spring-modulith-test/src/main/java/org/springframework/modulith/test/ApplicationModuleTest.java).

- **`@ApplicationModuleTest`** (from `spring-modulith-starter-test`) works like `@SpringBootTest`, but limits auto-configuration, component scanning and entity scanning to the module the test class lives in.
  - Attributes: `mode`/`value` (`STANDALONE` (default), `DIRECT_DEPENDENCIES`, `ALL_DEPENDENCIES`), `extraIncludes`, `module`, `classes`, `webEnvironment` and `verifyAutomatically` (default `true`, so it also verifies the module structure).
  - Beans from modules outside the slice must be provided as `@MockitoBean`, or bootstrap fails.
- **`@ModuleSlicing`** combines module slicing with Boot slices such as `@DataJpaTest`.
- **`Scenario`** is a test-method parameter for event-driven flows: `publish(…)` or `stimulate(…)`, then `andWaitForEventOfType(…)` or `andWaitForStateChange(…)`, then `toArrive…()` or `notToArrive()` (2.2).
- **Change-aware test execution**: `spring-modulith-junit` runs only the tests of changed modules and the modules that depend on them, based on Git changes. Settings are `spring.modulith.test.file-modification-detector` and `spring.modulith.test.reference-commit`. It falls back to running everything when build files change or when run from an IDE.

## What ArchUnit verifies on its own

Source: [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html).

- **Artifacts**: `com.tngtech.archunit:archunit` (core) and `archunit-junit4`, `archunit-junit5` or `archunit-junit6` (engine with `@AnalyzeClasses` and `@ArchTest`). There is no Gradle plugin. The guide mentions only a third-party Maven plugin.
- **Dependency model**: field, method and constructor accesses, member types (fields, parameters, return types), supertypes, annotations, generic signature types, and (since 1.5.0) caught exception types.
- **Rules relevant to #493**:
  - **Slices**: `slices().matching("ch.no1hardy.service.(*)..").should().beFreeOfCycles()`, or `.notDependOnEachOther()`. This is the equivalent of Modulith rule 1.
  - **Modules API**: `ModuleRuleDefinition.modules().definedByPackages(…)` or `.definedByAnnotation(…)` with `.beFreeOfCycles()`, `.respectTheirAllowedDependenciesDeclaredIn("attr", …)` and `.onlyDependOnEachOtherThroughPackagesDeclaredIn("attr")`. These are the equivalents of Modulith rules 2 and 3, but driven by an annotation *we* write.
  - **Layers inside a module**: `layeredArchitecture().consideringAllDependencies().layer(…).definedBy(…).whereLayer(…).mayOnlyBeAccessedByLayers(…)`, and `onionArchitecture()`. Modulith does not offer this, except through jMolecules.
  - **PlantUML**: `classes().should(adhereToPlantUmlDiagram(url, …))` checks code against a hand-drawn component diagram. This is the reverse of Modulith, which generates the diagram from code.
  - **General class rules**: naming, annotations, "entities only in X", "controllers must not touch repositories", "no field injection", and so on.
- **`FreezingArchRule.freeze(rule)`** records existing violations in a text store, configured by `freeze.store.default.path` and `freeze.store.default.allowStoreCreation` in `archunit.properties`, and fails only on *new* violations. This is useful for turning a rule on before the migration is finished.
- **Caching**: `@AnalyzeClasses` reuses imported classes per location. `ImportOption.Predefined.DO_NOT_INCLUDE_TESTS` excludes test classes.

## What each can enforce in our Gradle build

Both run as tests in `:service:test`. Neither runs at compile time, and `@ApplicationModule` annotations do nothing until the verifying test runs.

| Boundary rule | Spring Modulith | Plain ArchUnit |
|---|---|---|
| No cycles between domain modules | `verify()`, by convention | `slices().…beFreeOfCycles()` or `modules().beFreeOfCycles()` |
| Other modules use only a module's API package | `verify()` (sub-packages internal by default) | `modules().onlyDependOnEachOtherThroughPackagesDeclaredIn(…)` with a custom annotation, or hand-written `noClasses()` rules |
| Declared allowed dependencies per module | `@ApplicationModule(allowedDependencies)` | `modules().respectTheirAllowedDependenciesDeclaredIn(…)` with a custom annotation |
| Narrow ports (`user :: lookup`) | `@NamedInterface` | Expressible with custom package conventions and rules |
| Layering inside a module (controller → service → repository) | Not built in (jMolecules hexagonal/onion only) | `layeredArchitecture()` |
| Naming, annotation and placement rules | No | Yes |
| Ratchet: block new violations, tolerate old ones | `detectViolations().filter(…)` (manual) | `FreezingArchRule` |
| Generated module diagrams and canvases | `Documenter` | No (only checks code against a diagram) |
| Module-scoped Spring test contexts | `@ApplicationModuleTest` | No |
| Durable cross-module async events | Event Publication Registry | No |

## Cost

| | Spring Modulith | ArchUnit only |
|---|---|---|
| **Dependencies** | Import `spring-modulith-bom` 2.2.0. Minimum: `spring-modulith-starter-test` (test scope: docs, test and core with ArchUnit). `@ApplicationModule` and `@NamedInterface` live in `spring-modulith-api`, which is compile scope and has no runtime behaviour. Optional: `spring-modulith-starter-core` (adds runtime, apt and moments), `spring-modulith-starter-jpa` (registry plus a DB table). | One test-scope dependency, `archunit-junit6` 1.5.1 or `archunit` 1.5.1 |
| **Build time** | Not measured here. One ArchUnit class import of `ch.no1hardy.service` per verify or documenter test, plus a Spring context per `@ApplicationModuleTest` slice, possibly fewer than today's full `@SpringBootTest` contexts. | Not measured here. One class import, cached per location across `@ArchTest` rules. |
| **Learning curve** | Small set of concepts: module = package, internal = sub-package, `allowedDependencies`, named interfaces, event listener semantics (`@ApplicationModuleListener` is async and `REQUIRES_NEW`, which changes transaction behaviour if adopted). | Fluent rule DSL. Each rule must be designed and written; no conventions are given. |
| **Lock-in** | Low for verification: annotations on `package-info.java` plus one test, and removing them leaves plain packages. Medium if we adopt the registry or `@ApplicationModuleListener`: a DB table, Jackson-serialized events, and async semantics the code then relies on. | Very low: test code only. |

## Points for the #493 decisions

- Neither tool helps until the packages are split by domain. With today's layer packages, Modulith would treat `controller`, `service`, `model` and the rest as modules.
- Modulith's default "direct sub-package = module" convention and "sub-packages are internal" rule fix a package anatomy. The target structure should be decided with that in mind if we adopt it: shared cross-cutting code becomes either a `sharedModules` entry or a module everyone may depend on.
- Domain events and the registry are separate choices. Synchronous `@EventListener` decoupling needs no new table. The registry and `@ApplicationModuleListener` bring async behaviour and a schema change, which falls under the "migration tool that runs on production" constraint.
- Migration path either way: start with an OPEN module or `detectViolations().filter(…)` (Modulith), or `FreezingArchRule` (ArchUnit), and ratchet down.
