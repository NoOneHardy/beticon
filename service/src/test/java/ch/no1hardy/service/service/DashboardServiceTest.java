package ch.no1hardy.service.service;

import ch.no1hardy.service.GameHelper;
import ch.no1hardy.service.SecurityHelper;
import ch.no1hardy.service.exception.user.NotLoggedInException;
import ch.no1hardy.service.front.dashboard.GlobalStatistics;
import ch.no1hardy.service.front.dashboard.Statistics;
import ch.no1hardy.service.front.home.HomeRes;
import ch.no1hardy.service.model.game.Bet;
import ch.no1hardy.service.model.game.Game;
import ch.no1hardy.service.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DashboardServiceTest {
    @Autowired
    private DashboardService service;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private GameService gameService;

    @Test
    @DisplayName("getGlobalStatistics() - should return zero statistics if no users exist")
    void shouldReturnZeroStatisticsIfNoUsersExist() {
        when(userService.listRaw()).thenReturn(List.of());

        GlobalStatistics globalStats = service.getGlobalStatistics();

        assertEquals(0, globalStats.totalPoints());
        assertEquals(0, globalStats.exactBets());
        assertEquals(0, globalStats.wastedJokers());
    }

    @Test
    @DisplayName("getUserSummary() - should throw NotLoggedInException if no user is found")
    void shouldThrowNotLoggedInExceptionIfNoUserIsFound() {
        SecurityHelper.mockNoLogin(authService);
        assertThrows(NotLoggedInException.class, service::getUserSummary);
    }

    @Test
    @DisplayName("getStatistics() - should throw NotLoggedInException if no user is logged in")
    void shouldThrowNotLoggedInExceptionIfNoUserLoggedInForStatistics() {
        SecurityHelper.mockNoLogin(authService);
        assertThrows(NotLoggedInException.class, service::getStatistics);
    }

    @Test
    @DisplayName("getStatistics(User) - should return correct statistics for a user with active bets")
    void shouldReturnCorrectStatisticsForUserWithActiveBets() {
        User user = new User();
        user.setId("user-1");

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");

        GameHelper.createBet("bet-1", user, game1, 2, 3);
        GameHelper.createBet("bet-2", user, game2, 1, 1, 2);

        GameHelper.createScore("score-1", game1, 2, 3);
        GameHelper.createScore("score-2", game2, 0, 4);

        Statistics stats = service.getStatistics(user);

        assertEquals(7, stats.totalGoalsBet());
        assertEquals(1, stats.exactBets());
        assertEquals(1, stats.wastedJokers());
    }

    @Test
    @DisplayName("getStatistics(User) - should return zero statistics for a user with no active bets")
    void shouldReturnZeroStatisticsForUserWithNoActiveBets() {
        User user = new User();
        user.setId("user-1");
        user.setBets(List.of());

        Statistics stats = service.getStatistics(user);

        assertEquals(0, stats.totalGoalsBet());
        assertEquals(0, stats.exactBets());
        assertEquals(0, stats.wastedJokers());
    }

    @Test
    @DisplayName("getStatistics(User) - should return zero statistics for a user with inactive bets")
    void shouldReturnZeroStatisticsForUserWithInactiveBets() {
        User user = new User();
        user.setId("user-1");

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");

        Bet bet1 = GameHelper.createBet("bet-1", user, game1, 2, 3);
        bet1.setDeletedAt(LocalDateTime.now());
        Bet bet2 = GameHelper.createBet("bet-2", user, game2, 1, 1);
        bet2.setDeletedAt(LocalDateTime.now());

        Statistics stats = service.getStatistics(user);

        assertEquals(0, stats.totalGoalsBet());
        assertEquals(0, stats.exactBets());
        assertEquals(0, stats.wastedJokers());
    }

    @Test
    @DisplayName("getStatistics(User) - should handle games without result gracefully")
    void shouldHandleGamesWithoutResultGracefully() {
        User user = new User();
        user.setId("user-1");

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");

        GameHelper.createBet("bet-1", user, game1, 2, 3);
        GameHelper.createBet("bet-2", user, game2, 1, 1);

        Statistics stats = service.getStatistics(user);

        assertEquals(7, stats.totalGoalsBet());
        assertEquals(0, stats.exactBets());
        assertEquals(0, stats.wastedJokers());
    }

    @Test
    @DisplayName("getTotalGoalsBet(User) - should return total goals for active bets")
    void shouldReturnTotalGoalsForActiveBets() {
        User user = new User();

        GameHelper.createBet("bet-1", user, GameHelper.createGame("game-1"), 2, 3);
        GameHelper.createBet("bet-2", user, GameHelper.createGame("game-2"), 1, 1);

        int totalGoals = service.getTotalGoalsBet(user);

        assertEquals(7, totalGoals);
    }

    @Test
    @DisplayName("getTotalGoalsBet(User) - should return zero if user has no bets")
    void shouldReturnZeroIfUserHasNoBetsInGetTotalGoalsBet() {
        User user = new User();
        user.setBets(List.of());

        int totalGoals = service.getTotalGoalsBet(user);

        assertEquals(0, totalGoals);
    }

    @Test
    @DisplayName("getTotalGoalsBet(User) - should return zero if all bets are inactive")
    void shouldReturnZeroIfAllBetsAreInactive() {
        User user = new User();

        Bet bet1 = GameHelper.createBet("bet-1", user, GameHelper.createGame("game-1"), 2, 3);
        bet1.setDeletedAt(LocalDateTime.now());
        Bet bet2 = GameHelper.createBet("bet-2", user, GameHelper.createGame("game-2"), 1, 1);
        bet2.setDeletedAt(LocalDateTime.now());

        int totalGoals = service.getTotalGoalsBet(user);

        assertEquals(0, totalGoals);
    }

    @Test
    @DisplayName("getExactBets(User) - should count active bets with exact score matches")
    void shouldReturnCorrectCountForActiveBetsWithExactScoreMatches() {
        User user = new User();

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");

        GameHelper.createBet("bet-1", user, game1, 2, 3);
        GameHelper.createBet("bet-2", user, game2, 1, 1);

        GameHelper.createScore("score-1", game1, 2, 3);
        GameHelper.createScore("score-2", game2, 1, 1);

        int exactBets = service.getExactBets(user);

        assertEquals(2, exactBets);
    }

    @Test
    @DisplayName("getExactBets(User) - should return zero if no bets match exact scores")
    void shouldReturnZeroIfNoBetsMatchExactScores() {
        User user = new User();

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");

        GameHelper.createBet("bet-1", user, game1, 2, 3);
        GameHelper.createBet("bet-2", user, game2, 1, 1);

        GameHelper.createScore("score-1", game1, 0, 3);
        GameHelper.createScore("score-2", game2, 2, 2);

        int exactBets = service.getExactBets(user);

        assertEquals(0, exactBets);
    }

    @Test
    @DisplayName("getExactBets(User) - should return zero if user has no active bets")
    void shouldReturnZeroIfUserHasNoActiveBets() {
        User user = new User();
        user.setBets(List.of());

        int exactBets = service.getExactBets(user);

        assertEquals(0, exactBets);
    }

    @Test
    @DisplayName("getExactBets(User) - should return zero if all bets are inactive")
    void shouldReturnZeroIfAllBetsAreInactiveInExactBets() {
        User user = new User();

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");

        Bet bet1 = GameHelper.createBet("bet-1", user, game1, 2, 3);
        bet1.setDeletedAt(LocalDateTime.now());
        Bet bet2 = GameHelper.createBet("bet-2", user, game2, 1, 1);
        bet2.setDeletedAt(LocalDateTime.now());

        int exactBets = service.getExactBets(user);

        assertEquals(0, exactBets);
    }

    @Test
    @DisplayName("getExactBets(User) - should handle null game results gracefully")
    void shouldHandleNullGameResultsGracefully() {
        User user = new User();
        GameHelper.createBet("bet-1", user, GameHelper.createGame("game-1"), 2, 3);
        GameHelper.createBet("bet-2", user, GameHelper.createGame("game-2"), 1, 1);

        int exactBets = service.getExactBets(user);

        assertEquals(0, exactBets);
    }

    @Test
    @DisplayName("getWastedJokers(User) - should return zero if user has no bets")
    void shouldReturnZeroIfUserHasNoBetsInWastedJokers() {
        User user = new User();
        user.setBets(List.of());

        int wastedJokers = service.getWastedJokers(user);

        assertEquals(0, wastedJokers);
    }

    @Test
    @DisplayName("getWastedJokers(User) - should return zero if all bets are inactive")
    void shouldReturnZeroIfAllBetsAreInactiveInWastedJokers() {
        User user = new User();

        Bet bet1 = GameHelper.createBet("bet-1", user, GameHelper.createGame("game-1"), 2, 3, 2);
        bet1.setDeletedAt(LocalDateTime.now());
        Bet bet2 = GameHelper.createBet("bet-2", user, GameHelper.createGame("game-2"), 1, 1, 3);
        bet2.setDeletedAt(LocalDateTime.now());

        int wastedJokers = service.getWastedJokers(user);

        assertEquals(0, wastedJokers);
    }

    @Test
    @DisplayName("getWastedJokers(User) - should return zero if no jokers are used")
    void shouldReturnZeroIfNoJokersUsed() {
        User user = new User();
        GameHelper.createBet("bet-1", user, GameHelper.createGame("game-1"), 2, 3);
        GameHelper.createBet("bet-2", user, GameHelper.createGame("game-2"), 1, 1);

        int wastedJokers = service.getWastedJokers(user);

        assertEquals(0, wastedJokers);
    }

    @Test
    @DisplayName("getWastedJokers(User) - should return correct count for bets with wasted jokers")
    void shouldReturnCorrectCountForBetsWithWastedJokers() {
        User user = new User();

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");

        GameHelper.createBet("bet-1", user, game1, 2, 3, 2);
        GameHelper.createBet("bet-2", user, game2, 1, 1, 3);

        GameHelper.createScore("score-1", game1, 4, 0);
        GameHelper.createScore("score-2", game2, 0, 3);

        int wastedJokers = service.getWastedJokers(user);

        assertEquals(2, wastedJokers);
    }

    @Test
    @DisplayName("getWastedJokers(User) - should count a wasted triple joker as 1 wasted joker")
    void shouldCountTripleJokerAs1WastedJoker() {
        User user = new User();

        Game game1 = GameHelper.createGame("game-1");
        GameHelper.createBet("bet-1", user, game1, 2, 3, 3);
        GameHelper.createScore("score-1", game1, 4, 0);

        int wastedJokers = service.getWastedJokers(user);

        assertEquals(1, wastedJokers);
    }

    @Test
    @DisplayName("getWastedJokers(User) - should count a wasted double joker as 1 wasted joker")
    void shouldCountDoubleJokerAs1WastedJokers() {
        User user = new User();

        Game game1 = GameHelper.createGame("game-1");
        GameHelper.createBet("bet-1", user, game1, 2, 3, 2);
        GameHelper.createScore("score-1", game1, 4, 0);

        int wastedJokers = service.getWastedJokers(user);

        assertEquals(1, wastedJokers);
    }

    @Test
    @DisplayName("getWastedJokers(User) - should not count jokers on bets that earned points")
    void shouldNotCountJokersOnBetsThatEarnedPoints() {
        User user = new User();

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");
        Game game3 = GameHelper.createGame("game-3");

        // exact with x2, correct winner only with x3, correct total goals only with x2
        GameHelper.createBet("bet-1", user, game1, 2, 3, 2);
        GameHelper.createBet("bet-2", user, game2, 1, 3, 3);
        GameHelper.createBet("bet-3", user, game3, 0, 3, 2);
        GameHelper.createScore("score-1", game1, 2, 3);
        GameHelper.createScore("score-2", game2, 0, 1);
        GameHelper.createScore("score-3", game3, 3, 0);

        assertEquals(0, service.getWastedJokers(user));
    }

    @Test
    @DisplayName("getGlobalStatistics() - should count exact bets and wasted jokers per bet across users")
    void shouldCountExactBetsAndWastedJokersPerBetGlobally() {
        User user1 = new User();
        user1.setPoints(10);
        User user2 = new User();
        user2.setPoints(5);

        Game game1 = GameHelper.createGame("game-1");
        Game game2 = GameHelper.createGame("game-2");
        GameHelper.createBet("bet-1", user1, game1, 4, 0, 3);
        GameHelper.createBet("bet-2", user2, game1, 0, 3, 1);
        GameHelper.createBet("bet-3", user2, game2, 0, 1, 2);
        GameHelper.createScore("score-1", game1, 0, 3);
        GameHelper.createScore("score-2", game2, 4, 0);

        when(userService.listRaw()).thenReturn(List.of(user1, user2));

        GlobalStatistics stats = service.getGlobalStatistics();

        assertEquals(15, stats.totalPoints());
        assertEquals(1, stats.exactBets());
        assertEquals(2, stats.wastedJokers());
    }

    @Test
    @DisplayName("participationFee - should load from application properties")
    void participationFee01() throws Exception {
        var field = DashboardService.class.getDeclaredField("participationFee");
        field.setAccessible(true);
        Object value = field.get(service);

        assertEquals(10, value);
    }

    @Test
    @DisplayName("getGamesCount() - should count how many games are online")
    void getGamesCount01() {
        when(gameService.getAllActiveGames()).thenReturn(List.of(new Game(), new Game(), new Game()));
        assertEquals(3, service.getGamesCount());
    }

    @Test
    @DisplayName("getGamesCount() - should return null if there are no games")
    void getGamesCount02() {
        when(gameService.getAllActiveGames()).thenReturn(List.of());
        assertEquals(0, service.getGamesCount());
    }

    @Test
    @DisplayName("getParticipantsCount() - should return number of approved players")
    void getParticipantsCount01() {
        when(userService.listConfirmedRaw()).thenReturn(List.of(new User(), new User(), new User()));
        assertEquals(3, service.getParticipantsCount());
    }

    @Test
    @DisplayName("getParticipantsCount() - should return 0 if no players where found")
    void getParticipantsCount02() {
        when(userService.listConfirmedRaw()).thenReturn(List.of());
        assertEquals(0, service.getParticipantsCount());
    }

    @Test
    @DisplayName("getJackpot() - should calculate using particpants count and participation fee")
    void getJackpot01() {
        when(userService.listConfirmedRaw()).thenReturn(List.of(new User(), new User()));
        assertEquals(2, service.getParticipantsCount());
        assertEquals(20, service.getJackpot());
    }

    @Test
    @DisplayName("getHomeData() - should bundle data for home screen as HomeRes record")
    void getHomeData01() {
        when(userService.listConfirmedRaw()).thenReturn(List.of(new User(), new User()));
        when(gameService.getAllActiveGames()).thenReturn(List.of(new Game()));

        assertEquals(new HomeRes(20, 2, 1), service.getHomeData());
    }
}
