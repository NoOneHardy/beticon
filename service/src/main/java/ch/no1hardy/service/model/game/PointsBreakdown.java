package ch.no1hardy.service.model.game;

/**
 * The itemised Points of one Bet, before the Joker is applied.
 */
public record PointsBreakdown(int correctWinner, int correctGoalsHome, int correctGoalsGuest, int correctGoalsTotal, int joker) {
    public int total() {
        return (correctWinner + correctGoalsHome + correctGoalsGuest + correctGoalsTotal) * joker;
    }

    public boolean isExact() {
        return correctGoalsHome > 0 && correctGoalsGuest > 0;
    }

    public boolean isWastedJoker() {
        return joker > 1 && total() == 0;
    }
}
