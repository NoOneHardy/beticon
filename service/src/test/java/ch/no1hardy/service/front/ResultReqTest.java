package ch.no1hardy.service.front;

import ch.no1hardy.service.front.game.ResultReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultReqTest {
    @Test
    @DisplayName("should validate score request with valid scores")
    public void shouldValidateScoreRequestWithValidScores() {
        assertTrue(new ResultReq("game-1", 2, 3).isValid());
    }

    @Test
    @DisplayName("should validate score request with null values")
    public void shouldValidateScoreRequestWithNullValues() {
        assertTrue(new ResultReq("game-1", null, 1).isValid());
        assertTrue(new ResultReq("game-1", 1, null).isValid());
    }

    @Test
    @DisplayName("should validate score request with negative values")
    public void shouldValidateScoreRequestWithNegativeValues() {
        assertFalse(new ResultReq("game-1", -4, 1).isValid());
        assertFalse(new ResultReq("game-1", 1, -4).isValid());
    }

    @Test
    @DisplayName("should clamp a missing score to 0 when the other one is set")
    public void shouldClampMissingScore() {
        ResultReq homeMissing = new ResultReq("game-1", null, 2);
        assertEquals(0, homeMissing.clampedScoreTeamHome());
        assertEquals(2, homeMissing.clampedScoreTeamGuest());

        ResultReq guestMissing = new ResultReq("game-1", 2, null);
        assertEquals(2, guestMissing.clampedScoreTeamHome());
        assertEquals(0, guestMissing.clampedScoreTeamGuest());
    }

    @Test
    @DisplayName("should keep both scores missing when none is set")
    public void shouldKeepBlankScoresMissing() {
        ResultReq blank = new ResultReq("game-1", null, null);
        assertTrue(blank.isNull());
        assertNull(blank.clampedScoreTeamHome());
        assertNull(blank.clampedScoreTeamGuest());
    }

    @Test
    @DisplayName("should treat a missing score as 0 when checking for a tie")
    public void shouldCheckTieOnClampedScores() {
        assertTrue(new ResultReq("game-1", 1, 1).isTie());
        assertTrue(new ResultReq("game-1", null, 0).isTie());
        assertFalse(new ResultReq("game-1", null, 2).isTie());
        assertFalse(new ResultReq("game-1", null, null).isTie());
    }
}
