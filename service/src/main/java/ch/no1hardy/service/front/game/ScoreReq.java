package ch.no1hardy.service.front.game;

import ch.no1hardy.service.model.game.Scoreline;

/**
 * The goals of a Bet or Result as sent by the client. A missing score counts as 0 once the other one is set.
 */
public interface ScoreReq {
    String game();

    Integer scoreTeamHome();

    Integer scoreTeamGuest();

    default boolean isValid() {
        return (scoreTeamHome() == null || scoreTeamHome() >= 0) &&
                (scoreTeamGuest() == null || scoreTeamGuest() >= 0);
    }

    default boolean isNull() {
        return scoreTeamHome() == null && scoreTeamGuest() == null;
    }

    default boolean isTie() {
        return !isNull() && scoreline().isTie();
    }

    /**
     * @return the goals as a Scoreline, a missing score counted as 0; null when both are missing
     */
    default Scoreline scoreline() {
        if (isNull()) return null;
        return new Scoreline(
                scoreTeamHome() == null ? 0 : scoreTeamHome(),
                scoreTeamGuest() == null ? 0 : scoreTeamGuest());
    }
}
