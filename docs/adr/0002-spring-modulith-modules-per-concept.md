# The service is split into Spring Modulith modules, one per domain concept

The service is organised so that everything about one domain concept sits in one place. Each concept gets one Spring Modulith application module, a top-level package under `ch.no1hardy.service` named after its Glossary term in singular lowercase (`bet`, `game`, `resultentry`). We replaced the split by layer (`controller/`, `service/`, `mapper/`, `model/`, `front/`) because finding everything about one concept meant opening five packages. We chose Modulith over hexagonal modules because every port would have had exactly one adapter.

A module's root package holds only its interface: the service other modules call, plus the types and events they need. Everything else sits in internal sub-packages split by class role. `CODING_STANDARDS.md` lists the usual roles (`web`, `persistence`, `mapping`, `domain`) as a guide. A sub-package is visible to other modules only when it is marked `@NamedInterface`.

## Considered Options

- **Flat module package, package-private by default**: the compiler would enforce the boundary, but the controller, DTOs, mapper, entities and repositories would all lie unsorted in one folder. We gave up compile-time protection to keep the role sub-packages.
- **`allowedDependencies` on every module**: rejected. Whoever adds a dependency can add it to the list in the same change, so the list stops nothing.

## Consequences

- Classes in different sub-packages of one module must be `public` to see each other. Only the Modulith `verify()` test in `:service:test` guards the boundary. It fails on cycles between modules and on access to another module's internal types, but not at compile time.
- The generated module diagram is committed under `service/docs/modules/`, and a test fails when it is out of date. A new dependency between modules therefore shows up as a changed line in the PR diff. Review of that diff is the only gate on new dependencies.
