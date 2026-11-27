# Beticon

A betting game for football tournaments. Users predict the score of each Game and earn Points when the Result is known.

## Language

### Tournament

**Game**:
The encounter of two Teams, one home and one guest, at a set time. It has at most one Result.
_Avoid_: Match, fixture, encounter

**Team**:
A side that plays Games in the tournament, such as a national team.
_Avoid_: Nation, country

**Group**:
A stage of the tournament that holds Games: a group-stage group or a knockout round. The word is overloaded and will be renamed.
_Avoid_: Stage, round (until the rename settles the term)

### Betting

**Bet**:
A user's prediction of the final score of one Game, optionally carrying a Joker.
_Avoid_: Tip, prediction

**Result**:
The actual score of a Game at the end of play, including extra time. For a knockout Game that ends level, the winner of the penalty shoot-out is credited with one extra goal, so a knockout Result is never a tie.
_Avoid_: Score (in code, `Score` is the entity holding a Result), outcome

**Scoreline**:
The home and guest goals of one Game, as predicted by a Bet or recorded as a Result. Scoring compares the Bet's Scoreline with the Result's.
_Avoid_: Score (that means the Result entity in code), goals pair

**Joker**:
A multiplier (×2 or ×3) a user attaches to a Bet to multiply its Points. Each user has a limited number per tournament.
_Avoid_: Booster, multiplier

**Joker limit**:
The most ×2 Jokers, and separately the most ×3 Jokers, one user may attach to Bets in one tournament.
_Avoid_: Max jokers, jDoubleMax

**Remaining Jokers**:
How many ×2 Jokers, and separately how many ×3 Jokers, a user can still attach to Bets in one tournament: their Joker limit minus the Jokers already on their Bets. It goes up again when a Bet loses its Joker.
_Avoid_: Available jokers, jDouble

**Joker quota**:
A user's Joker limit and Remaining Jokers, for ×2 and for ×3, in one tournament.
_Avoid_: Available jokers, joker allowance

### Scoring

**Scoring**:
The rule that turns a Bet and its Game's Result into Points. It depends only on the Bet and the Result.
_Avoid_: Bet evaluation, point calculation

**Points**:
What a Bet earns through Scoring. A user's total Points is the sum of the Points of all their Bets.
_Avoid_: Score (that means goals), credits

**Points breakdown**:
The itemised Points of one Bet: correct winner or tie (5), correct home goals (2), correct guest goals (2), correct total goals (1), all multiplied by the Joker.
_Avoid_: Detailed points, Punkteverteilung (UI label only)

**Exact Bet**:
A Bet whose home and guest goals both match the Result, which earns the full Points.
_Avoid_: Correct game, perfect bet

**Wasted Joker**:
A Joker on a Bet that earned 0 Points. Counted per Bet, so a wasted ×3 Joker is one Wasted Joker.

### Running a tournament

**Host**:
The person who runs a Beticon tournament for a group of players, usually as its admin. If a group plays for stakes, the Host collects and pays out the money outside the app.
_Avoid_: Organiser, game master

**Application**:
A new user's request to take part in the tournament, which the Host approves or denies. A user can only play once their Application is approved.
_Avoid_: Registration (that is signup), approval request
