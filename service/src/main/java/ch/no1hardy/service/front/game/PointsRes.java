package ch.no1hardy.service.front.game;

import lombok.Builder;
import lombok.Data;

/**
 * The Points breakdown of a scored Bet. Items that are not awarded (and a Joker of 1) are null.
 */
@Data
@Builder
public class PointsRes {
    private Integer correctWinner;
    private Integer correctGoalsHome;
    private Integer correctGoalsGuest;
    private Integer correctGoalsTotal;
    private Integer joker;
    private int total;
}
