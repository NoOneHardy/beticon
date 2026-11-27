# Modules call along the dependency graph, and talk against it only through events or interfaces they own

A module calls another module's public API only in the direction of their dependency. When control has to flow against that direction, the upstream module either announces a fact with a domain event that downstream modules listen to, or it defines an interface that a downstream module implements. The interface is the heaviest of the three, so we use it only when neither a call nor an event fits. Only records cross a module boundary: ids plus the facts the receiver needs, never JPA entities. They replace the `getRaw*` and `listRaw` methods. We chose this because Modulith's `verify()` rejects cycles, and four of the dependencies the modules need (result entry → rescoring, auth → user credentials, approval → notification, new Game or Result → notification) would otherwise form them.

Events are handled synchronously, with no event publication registry. Rescoring runs as a plain `@EventListener` inside the result-entry transaction, so a failed rescore still rolls back the Result. Async `@ApplicationModuleListener` would change when Points, notifications and GlobalData exist relative to the API response, and the behaviour is frozen during the migration.

Every write use case is `@Transactional` at its entry point into the module, so a write that fails halfway can't leave half its changes saved. Notifications are not events. `notification` is a generic delivery module (in-app and email) that depends only on `preferences`, and the module that owns a fact calls it with the recipients and its own content. When a transaction is active, `send` delays delivery until after the commit and drops the notifications on rollback, so callers can call it anywhere inside their use case.

## Considered Options

- **`game` calls `scoring` to rescore**: a cycle. `scoring` depends on `game` and `bet` (Points are stored on the Bet, ADR 0001), and `bet` depends on `game`.
- **An interface owned by `game` for rescoring**: it would read like a call, but it needs one more type for a single listener. The event shows up in the module diagram just as well.
- **`notification` owns every trigger and listens to events**: it would depend on `game`, `user` and the Leaderboard, and each sender's content would live far from the fact it reports.
- **A notification broadcast that resolves "every player" through an interface implemented by `user`**: rejected. The sender asks `user` for the recipient ids itself, which saves an interface.

## Consequences

- `auth` sits at the bottom of the graph. It owns a `Credentials` record and a `CredentialsLookup` interface that `user` implements, and `getLoggedInUser()` returns the logged-in user's id or principal, not the `User` entity.
- `notification` owns the catalogue of notification types, the opt-out check and the `/preferences/notifications` endpoints. `preferences` stores per-user values by key without knowing what the keys mean.
- Adding async or guaranteed delivery later means adding the event publication registry and its table, and accepting the change in timing.
