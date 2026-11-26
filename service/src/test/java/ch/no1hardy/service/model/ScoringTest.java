package ch.no1hardy.service.model;

import ch.no1hardy.service.model.game.PointsBreakdown;
import ch.no1hardy.service.model.game.Scoreline;
import ch.no1hardy.service.model.game.Scoring;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoringTest {
    private static PointsBreakdown score(int betHome, int betGuest, int resHome, int resGuest, int joker) {
        return Scoring.score(new Scoreline(betHome, betGuest), new Scoreline(resHome, resGuest), joker);
    }

    /** Cases from the frontend points-service spec: bet home/guest, result home/guest, expected points (Joker x1). */
    @ParameterizedTest(name = "bet {0}:{1} result {2}:{3} -> {4}")
    @CsvSource({
            "1,2,1,2,10",
            "2,1,2,1,10",
            "0,0,0,0,10",
            "2,1,2,0,7",
            "1,2,0,2,7",
            "2,2,1,1,5",
            "2,1,3,0,6",
            "1,2,0,3,6",
            "2,1,4,0,5",
            "1,2,2,4,5",
            "2,1,0,1,2",
            "1,2,1,0,2",
            "2,1,0,3,1",
            "1,4,3,2,1",
            "2,1,0,0,0"
    })
    @DisplayName("score() - should give the expected points for every Joker")
    void points(int betHome, int betGuest, int resHome, int resGuest, int expected) {
        assertEquals(expected, score(betHome, betGuest, resHome, resGuest, 1).total());
        assertEquals(expected * 2, score(betHome, betGuest, resHome, resGuest, 2).total());
        assertEquals(expected * 3, score(betHome, betGuest, resHome, resGuest, 3).total());
    }

    @Test
    @DisplayName("score() - should itemise exact bet")
    void exactBet() {
        PointsBreakdown p = score(2, 1, 2, 1, 1);
        assertEquals(5, p.correctWinner());
        assertEquals(2, p.correctGoalsHome());
        assertEquals(2, p.correctGoalsGuest());
        assertEquals(1, p.correctGoalsTotal());
        assertTrue(p.isExact());
    }

    @Test
    @DisplayName("score() - should itemise each item alone")
    void singleItems() {
        PointsBreakdown winner = score(2, 1, 4, 0, 1);
        assertEquals(new PointsBreakdown(5, 0, 0, 0, 1), winner);
        assertFalse(winner.isExact());

        assertEquals(new PointsBreakdown(0, 0, 2, 0, 1), score(2, 1, 0, 1, 1));
        assertEquals(new PointsBreakdown(0, 2, 0, 0, 1), score(1, 2, 1, 0, 1));
        assertEquals(new PointsBreakdown(0, 0, 0, 1, 1), score(2, 1, 0, 3, 1));
    }

    @Test
    @DisplayName("score() - should treat a tie with other goals as correct winner")
    void tie() {
        assertEquals(new PointsBreakdown(5, 0, 0, 0, 1), score(2, 2, 1, 1, 1));
        assertEquals(new PointsBreakdown(0, 0, 0, 0, 1), score(1, 1, 3, 2, 1));
    }

    @Test
    @DisplayName("isWastedJoker() - true only for a Joker above 1 with 0 points")
    void wastedJoker() {
        assertTrue(score(2, 1, 0, 0, 2).isWastedJoker());
        assertTrue(score(2, 1, 0, 0, 3).isWastedJoker());
        assertFalse(score(2, 1, 0, 0, 1).isWastedJoker());
        assertFalse(score(2, 1, 0, 3, 2).isWastedJoker());
        assertFalse(score(2, 1, 2, 1, 3).isWastedJoker());
    }

    @Test
    @DisplayName("Scoreline - winner, tie and total checks")
    void scoreline() {
        Scoreline s = new Scoreline(3, 1);
        assertTrue(s.homeWins());
        assertFalse(s.guestWins());
        assertFalse(s.isTie());
        assertEquals(4, s.total());
        assertTrue(new Scoreline(0, 0).isTie());
        assertTrue(new Scoreline(0, 1).guestWins());
    }
}
