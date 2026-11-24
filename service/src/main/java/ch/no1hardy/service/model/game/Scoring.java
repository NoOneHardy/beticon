package ch.no1hardy.service.model.game;

/**
 * The rule that turns a Bet and its Game's Result into Points.
 */
public final class Scoring {
    static final int WINNER_POINTS = 5;
    static final int GOALS_POINTS = 2;
    static final int TOTAL_POINTS = 1;

    private Scoring() {
    }

    public static PointsBreakdown score(Scoreline bet, Scoreline result, int joker) {
        boolean correctWinner = (bet.homeWins() && result.homeWins())
                || (bet.guestWins() && result.guestWins())
                || (bet.isTie() && result.isTie());

        return new PointsBreakdown(
                correctWinner ? WINNER_POINTS : 0,
                bet.getHome().equals(result.getHome()) ? GOALS_POINTS : 0,
                bet.getGuest().equals(result.getGuest()) ? GOALS_POINTS : 0,
                bet.total() == result.total() ? TOTAL_POINTS : 0,
                joker
        );
    }
}
