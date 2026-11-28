# Modules reference each other by ID, and `scoring` owns its Points tables

A JPA entity references another entity only inside its own module. Across a module boundary it holds the other entity's ID in the same column (`Bet.userId` mapped to `user_id`, not `Bet.user`), and the foreign keys stay in the database. Inverse sides that cross a boundary (`User.bets`, `Game.bets`, `Team.gamesHome`, …) are removed. Code that walked them asks the owning module, which offers its lookups by ID in bulk (Bets by game ids, Teams by ids), so mapping a list doesn't make one call per item. We chose this because entities live in each module's internal packages and only records cross module APIs (ADR 0003), so a reference to another module's entity would break `verify()`. This also applies along allowed edges like `game → team`: `game` gets Team data from `team`'s API as records.

The Points move out of other modules' tables into tables owned by `scoring`: Points per Bet, keyed by the bet id, and per user the cached total and the last-reviewed snapshot, keyed by the user id. A Flyway migration moves the values and drops `bet.points`, `user.points` and `user.last_reviewed_points`. The total stays a cache that is recomputed, never adjusted (ADR 0001).

## Considered Options

- **Exposing entities as named interfaces along allowed edges**: it keeps `game.getTeamHome()` working, but entities would cross boundaries, and any module could change another module's entity.
- **`scoring` maps its own entity onto the `bet` and `user` tables**: no schema change, but two modules' entities would share a table, so ownership would be invisible in the schema and one entity could overwrite the other's columns.
- **`bet` and `user` offer calls that set Points**: `scoring → user` isn't an edge, and Points writes would be spread out again.

## Consequences

- The only schema change is the Points migration. Replacing an association with an ID keeps its column and foreign key.
- `bet` and `game` can no longer build responses that include Points or Bets themselves. Composing those responses is a read-model concern.
