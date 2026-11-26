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
        return !isNull() && new Scoreline(clampedScoreTeamHome(), clampedScoreTeamGuest()).isTie();
    }

    /**
     * @return the home score, 0 when only the guest score is set
     */
    default Integer clampedScoreTeamHome() {
        return scoreTeamHome() == null && scoreTeamGuest() != null ? Integer.valueOf(0) : scoreTeamHome();
    }

    /**
     * @return the guest score, 0 when only the home score is set
     */
    default Integer clampedScoreTeamGuest() {
        return scoreTeamGuest() == null && scoreTeamHome() != null ? Integer.valueOf(0) : scoreTeamGuest();
    }
}
