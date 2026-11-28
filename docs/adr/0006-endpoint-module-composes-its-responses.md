# The module that owns an endpoint builds its response, and asks downstream modules through interfaces it owns

Every controller stays in the module whose concept it serves, so a response that nests data from several modules is built by that endpoint's module. When the data it needs sits in a module downstream of it, it defines an interface for that data in its own API, and the module that owns the data implements it. `game` builds `BetGameRes` and `GroupRes` through two such interfaces. `bet` implements the one for the user's Bets, bet percentages and Joker quota, and `scoring` implements the one for Points breakdowns by bet id. `user` builds `UserRes` through a Points lookup that `scoring` implements. We chose this because navigability by concept is the goal of the module split (ADR 0002), and ADR 0005 took away the entity navigation that used to build these responses.

Screens and rankings that aren't one concept's data get their own modules. `leaderboard` builds the Leaderboard on every request from the Points totals and snapshots that `scoring` exposes, plus the players from `user`. It owns the ranking-change notification, which it sends when `scoring` publishes `PointsRescored` after rescoring for an entered Result. `dashboard` sits at the top of the graph and only composes: it owns `/dashboard` and `/home`, and every number on them is computed by the module that owns it. GlobalData is built once, when the response body is written, from the latest unread notifications that `notification` exposes. No write refreshes it.

## Considered Options

- **One read module at the top of the graph that builds every composed response**: every call would follow the arrows, but `GET /game` would leave `game`, and so would the write endpoints that return composed responses (`PUT /game/{id}/bet`, `PUT /group/{id}/results`, …). Each concept's web layer would be split across two modules.
- **`scoring` builds the composed responses**: it is the furthest downstream module that sees Games, Bets and Points, so it needs no interfaces. But `/game` and `/group` reads would live in a module named after something else, and `scoring` would pass Bet data through.
- **One combined lookup implemented by `scoring`**: one type fewer than the two `game` interfaces, but `scoring` would fetch and pass on Bet data it doesn't own.
- **The Leaderboard inside `scoring`**: one module fewer and no event. But the Glossary's Scoring depends only on the Bet and the Result, while the Leaderboard depends on players, and scoped leaderboards are coming.
- **`leaderboard` listens to `ResultChanged` directly**: it would have to run after `scoring`'s listener, an order held only by `@Order` annotations in two unrelated modules. Listening after the commit instead would compute every notification of a multi-Result upload from the final state.

## Consequences

- Each composed response has one builder, found under the endpoint's own module. The interfaces show up in the module diagram as edges from the implementing module to the endpoint's module.
- `PointsRescored` is synchronous and handled inside the result-entry transaction (ADR 0003). The `leaderboard` listener catches and logs its own failures, so a failed ranking notification doesn't roll back the Result.
- Scoped leaderboards will grow inside `leaderboard`.
