package ch.no1hardy.service.model;

import ch.no1hardy.service.model.game.Bet;
import ch.no1hardy.service.model.game.Scoreline;
import ch.no1hardy.service.model.game.Game;
import ch.no1hardy.service.model.game.PointsBreakdown;
import ch.no1hardy.service.model.game.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BetTest {
    Bet bet;
    Game game;
    Score score;

    @BeforeEach
    void beforeEach() {
        bet = new Bet();
        bet.setId("bet-1");
        bet.setScoreline(new Scoreline(2, 3));

        game = new Game();
        bet.setGame(game);
        game.setBets(List.of(bet));

        game.setId("game-1");

        score = new Score();
        game.setResult(score);
        score.setGame(game);

        score.setScoreline(new Scoreline(2, 3));
    }

    @Test
    @DisplayName("pointsBreakdown() - should score the bet against its game's result")
    void shouldScoreBetAgainstGameResult() {
        bet.setJoker(2);
        score.setScoreline(new Scoreline(1, 3));

        assertEquals(Optional.of(new PointsBreakdown(5, 0, 2, 0, 2)), bet.pointsBreakdown());
    }

    @Test
    @DisplayName("pointsBreakdown() - should be empty if the game has no result yet")
    void shouldHaveNoPointsBreakdownWithoutResult() {
        bet.setJoker(1);
        game.setResult(null);

        assertEquals(Optional.empty(), bet.pointsBreakdown());
    }
}
