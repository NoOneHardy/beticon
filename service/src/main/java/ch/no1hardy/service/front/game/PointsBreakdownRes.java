package ch.no1hardy.service.front.game;

import lombok.Builder;
import lombok.Data;

/**
 * The Points breakdown of a scored Bet. Items that are not awarded are null; the Joker multiplier (1, 2 or 3) is sent as-is.
 */
@Data
@Builder
public class PointsBreakdownRes {
    private Integer correctWinner;
    private Integer correctGoalsHome;
    private Integer correctGoalsGuest;
    private Integer correctGoalsTotal;
    private int joker;
    private int total;
}
