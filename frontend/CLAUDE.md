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

## Conventions

- Components are standalone, selectors use the `bet-` prefix in kebab-case (e.g. `bet-dashboard`).
- Business logic goes in `@Injectable()` services; models in `shared/model/` or a feature-specific `model/` folder.
- All components use the default OnPush change detection (Angular 22 default; no `changeDetection` override). Hold state in signals (`signal()`, `input()`, `computed()`, `store.selectSignal`, `toSignal`) rather than plain fields.
- Styling is SCSS (`theming.scss` for Material theming, component-scoped styles).

## Code quality

ESLint (`eslint.config.js`) enforces: 2-space indentation, single quotes (template literals allowed), no semicolons, `bet-` kebab-case selectors, warn on unused variables (except `_`-prefixed), Angular rules (e.g. no input renaming). TypeScript is in strict mode.

## Bundle budgets (enforced in CI)

Initial bundle: 500kB warning / 1MB error. Component styles: 2kB warning / 10kB error.
