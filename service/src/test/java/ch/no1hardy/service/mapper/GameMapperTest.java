package ch.no1hardy.service.mapper;

import ch.no1hardy.service.front.game.BetRes;
import ch.no1hardy.service.model.game.Scoreline;
import ch.no1hardy.service.model.game.Bet;
import ch.no1hardy.service.model.game.Game;
import ch.no1hardy.service.model.game.Score;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class GameMapperTest {
    @Autowired
    private GameMapper gameMapper;

    private Bet bet(int home, int guest, int joker, Score result) {
        Game game = new Game();
        game.setId("game-1");
        game.setResult(result);
        Bet bet = new Bet();
        bet.setId("bet-1");
        bet.setGame(game);
        bet.setScoreline(new Scoreline(home, guest));
        bet.setJoker(joker);
        game.setBets(List.of(bet));
        return bet;
    }

    private Score result(int home, int guest) {
        Score score = new Score();
        score.setScoreline(new Scoreline(home, guest));
        return score;
    }

    @Test
    @DisplayName("toDto(Bet) - should map points to null when the game has no result")
    void toDto01() {
        BetRes res = gameMapper.toDto(bet(2, 1, 2, null));
        assertThat(res.points()).isNull();
    }

    @Test
    @DisplayName("toDto(Bet) - should map the awarded items, joker and total of a scored bet")
    void toDto02() {
        BetRes res = gameMapper.toDto(bet(2, 1, 2, result(2, 1)));
        assertThat(res.points().correctWinner()).isEqualTo(5);
        assertThat(res.points().correctGoalsHome()).isEqualTo(2);
        assertThat(res.points().correctGoalsGuest()).isEqualTo(2);
        assertThat(res.points().correctGoalsTotal()).isEqualTo(1);
        assertThat(res.points().joker()).isEqualTo(2);
        assertThat(res.points().total()).isEqualTo(20);
    }

    @Test
    @DisplayName("toDto(Bet) - should map items that are not awarded to null and send the joker as-is")
    void toDto03() {
        BetRes res = gameMapper.toDto(bet(3, 0, 1, result(1, 0)));
        assertThat(res.points().correctWinner()).isEqualTo(5);
        assertThat(res.points().correctGoalsHome()).isNull();
        assertThat(res.points().correctGoalsGuest()).isEqualTo(2);
        assertThat(res.points().correctGoalsTotal()).isNull();
        assertThat(res.points().joker()).isEqualTo(1);
        assertThat(res.points().total()).isEqualTo(7);
    }
}
