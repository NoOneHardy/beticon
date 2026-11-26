package ch.no1hardy.service.front;

import ch.no1hardy.service.front.game.ResultReq;
import ch.no1hardy.service.model.game.Scoreline;
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
    @DisplayName("should count a missing score as 0 when the other one is set")
    public void shouldCountMissingScoreAsZero() {
        assertEquals(new Scoreline(0, 2), new ResultReq("game-1", null, 2).scoreline());
        assertEquals(new Scoreline(2, 0), new ResultReq("game-1", 2, null).scoreline());
    }

    @Test
    @DisplayName("should keep both scores missing when none is set")
    public void shouldKeepBlankScoresMissing() {
        ResultReq blank = new ResultReq("game-1", null, null);
        assertTrue(blank.isNull());
        assertNull(blank.scoreline());
    }

    @Test
    @DisplayName("should treat a missing score as 0 when checking for a tie")
    public void shouldCheckTieOnCountedScores() {
        assertTrue(new ResultReq("game-1", 1, 1).isTie());
        assertTrue(new ResultReq("game-1", null, 0).isTie());
        assertFalse(new ResultReq("game-1", null, 2).isTie());
        assertFalse(new ResultReq("game-1", null, null).isTie());
    }
}
