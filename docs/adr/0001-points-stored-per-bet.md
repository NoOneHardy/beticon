# Points are stored per Bet, and Scoring lives only in the backend

When a Result is entered or corrected, every Bet on that Game is scored and its Points are stored on the Bet. A user's total Points is the sum over their Bets. `User.points` is only a cache of that sum and is recomputed, never adjusted by adding or subtracting deltas. We replaced the earlier running counter (add Points on Result entry, subtract them before a correction) because it could not be checked or rebuilt and was easy to get wrong.

Scoring has one implementation, in the backend. The API sends each Bet's Points breakdown, and the frontend displays it without computing Points itself, so a rule change happens in one place.

## Consequences

- When a Scoring rule changes, or totals look wrong, the admin "re-score everything" action rebuilds every Bet's Points and every user's total from Bets and Results.
- The Points breakdown is not stored. It is recomputed from the Bet and the Result whenever it is needed.
