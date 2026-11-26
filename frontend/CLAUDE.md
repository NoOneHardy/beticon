# CLAUDE.md — Frontend

Angular 22 standalone-component app, managed with pnpm. Material 22, NgRx 22, date-fns, rxjs.

## Commands

Run from `frontend/` (or via `./gradlew :frontend:<task>` from the repo root):

```bash
pnpm start        # dev server on http://localhost:4200
pnpm build        # production build (Gradle: :frontend:build)
pnpm lint         # lint (Gradle: :frontend:lint); pnpm lint:fix to auto-fix
pnpm test-ci      # single Vitest run (Gradle: :frontend:test); pnpm test for watch mode
```

Tests use Vitest + jsdom via the Angular `unit-test` builder. Coverage is not configured. Spec files (`.spec.ts`) live next to the code.

## Structure (`src/app/`)

- `admin/` — admin panel (games/groups/teams/users/results); the only module using NgRx (`admin/store/`: actions, effects, reducers)
- `bet-management/`, `dashboard/`, `game/`, `leaderboard/`, `home/`, `root/` (layout), `settings/`, `user-management/`, `verification/` (email flow)
- `shared/` — shared components, services, utilities, models
- `model/` — TypeScript interfaces and models

Feature routes are lazy-loaded via `loadComponent` in `app.routes.ts`. App config is in `app.config.ts`; the API base URL is set by an HTTP interceptor. No `.env` is needed.

## Code quality

The lint rules live in `eslint.config.js`. TypeScript is in strict mode.

## Bundle budgets (enforced in CI)

Initial bundle: 500kB warning / 1MB error. Component styles: 2kB warning / 10kB error.
