package ch.no1hardy.service.front;

import ch.no1hardy.service.front.game.BetReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BetReqTest {
    @Test
    @DisplayName("should validate bet request with valid scores")
    public void shouldValidateBetRequestWithValidScores() {
        assertTrue(new BetReq("game-1", 2, 3, 1).isValid());
    }

    @Test
    @DisplayName("should validate bet request with null values")
    public void shouldValidateBetRequestWithNullValues() {
        assertTrue(new BetReq("game-1", null, 1, 1).isValid());
        assertTrue(new BetReq("game-1", 1, null, 1).isValid());
    }

    @Test
    @DisplayName("should validate bet request with negative values")
    public void shouldValidateBetRequestWithNegativeValues() {
        assertFalse(new BetReq("game-1", -4, 1, 1).isValid());
        assertFalse(new BetReq("game-1", 1, -4, 1).isValid());
    }

    @Test
    @DisplayName("should validate bet request with null joker")
    public void shouldValidateBetRequestWithNullJoker() {
        assertFalse(new BetReq("game-1", 4, 1, null).isValid());
        assertTrue(new BetReq("game-1", 4, 1, 2).isValid());
    }

    @Test
    @DisplayName("should keep a Joker of 1, 2 or 3")
    public void shouldKeepValidJoker() {
        assertEquals(1, new BetReq("game-1", 1, 0, 1).clampedJoker());
        assertEquals(2, new BetReq("game-1", 1, 0, 2).clampedJoker());
        assertEquals(3, new BetReq("game-1", 1, 0, 3).clampedJoker());
    }

    @Test
    @DisplayName("should clamp a Joker outside 1 to 3 to 1")
    public void shouldClampInvalidJoker() {
        assertEquals(1, new BetReq("game-1", 1, 0, 0).clampedJoker());
        assertEquals(1, new BetReq("game-1", 1, 0, 4).clampedJoker());
    }

    @Test
    @DisplayName("should clamp a missing score to 0 when the other one is set")
    public void shouldClampMissingScore() {
        BetReq homeMissing = new BetReq("game-1", null, 2, 1);
        assertEquals(0, homeMissing.clampedScoreTeamHome());
        assertEquals(2, homeMissing.clampedScoreTeamGuest());

        BetReq guestMissing = new BetReq("game-1", 2, null, 1);
        assertEquals(2, guestMissing.clampedScoreTeamHome());
        assertEquals(0, guestMissing.clampedScoreTeamGuest());
    }
}
