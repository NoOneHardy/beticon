package ch.no1hardy.service.front.game;

import lombok.Builder;

/**
 * The Points breakdown of a scored Bet. Items that are not awarded are null; the Joker multiplier (1, 2 or 3) is sent as-is.
 */
@Builder
public record PointsBreakdownRes(
        Integer correctWinner,
        Integer correctGoalsHome,
        Integer correctGoalsGuest,
        Integer correctGoalsTotal,
        int joker,
        int total
) {
}
