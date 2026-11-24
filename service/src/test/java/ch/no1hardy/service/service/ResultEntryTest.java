package ch.no1hardy.service.service;

import ch.no1hardy.service.front.game.ResultReq;
import ch.no1hardy.service.model.game.Bet;
import ch.no1hardy.service.model.game.BetRepository;
import ch.no1hardy.service.model.game.Game;
import ch.no1hardy.service.model.game.GameRepository;
import ch.no1hardy.service.model.group.Group;
import ch.no1hardy.service.model.group.GroupRepository;
import ch.no1hardy.service.model.team.Team;
import ch.no1hardy.service.model.team.TeamRepository;
import ch.no1hardy.service.model.user.User;
import ch.no1hardy.service.model.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Result entry through the game module's interface, with real repositories and real Scoring.
 * Notifications are stubbed because they are not under test here.
 */
@SpringBootTest
public class ResultEntryTest {
    @Autowired
    private GameService service;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private BetRepository betRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionTemplate transaction;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private AuthService authService;

    private Game game;
    private User exactUser;
    private User winnerUser;
    private User missUser;
    private Bet exactBet;
    private Bet winnerBet;
    private Bet missBet;

    @BeforeEach
    void beforeEach() {
        Group group = new Group();
        group.setName("Group " + UUID.randomUUID());
        group = groupRepository.save(group);

        game = new Game();
        game.setGroup(group);
        game.setTimestamp(LocalDateTime.now().minusDays(1));
        game.setTeamHome(createTeam());
        game.setTeamGuest(createTeam());
        game = gameRepository.save(game);

        exactUser = createUser();
        winnerUser = createUser();
        missUser = createUser();

        exactBet = createBet(exactUser, 2, 1, 1);
        winnerBet = createBet(winnerUser, 1, 0, 2);
        missBet = createBet(missUser, 0, 0, 1);

        when(authService.getLoggedInUser()).thenReturn(Optional.of(exactUser));
    }

    private Team createTeam() {
        Team team = new Team();
        team.setName("Team " + UUID.randomUUID());
        team.setShortName("TST");
        team.setFlag("flag");
        return teamRepository.save(team);
    }

    private User createUser() {
        String name = "user-" + UUID.randomUUID();
        User user = new User();
        user.setUsername(name);
        user.setPassword("password");
        user.setEmail(name + "@example.com");
        user.setFirstname("First");
        user.setLastname("Last");
        return userRepository.save(user);
    }

    private Bet createBet(User user, int home, int guest, int joker) {
        Bet bet = new Bet();
        bet.setUser(user);
        bet.setGame(game);
        bet.setScoreTeamHome(home);
        bet.setScoreTeamGuest(guest);
        bet.setJoker(joker);
        return betRepository.save(bet);
    }

    private void enterResult(Integer home, Integer guest) {
        enterResult(game, home, guest);
    }

    private void enterResult(Game target, Integer home, Integer guest) {
        ResultReq req = new ResultReq();
        req.setScoreTeamHome(home);
        req.setScoreTeamGuest(guest);
        service.uploadResult(target.getId(), req);
    }

    private Game createOtherGame() {
        Game other = new Game();
        other.setGroup(game.getGroup());
        other.setTimestamp(LocalDateTime.now().minusDays(2));
        other.setTeamHome(game.getTeamHome());
        other.setTeamGuest(game.getTeamGuest());
        return gameRepository.save(other);
    }

    private Integer storedPoints(Bet bet) {
        return betRepository.findById(bet.getId()).orElseThrow().getPoints();
    }

    private Integer total(User user) {
        return userRepository.findById(user.getId()).orElseThrow().getPoints();
    }

    private Integer lastReviewed(User user) {
        return userRepository.findById(user.getId()).orElseThrow().getLastReviewedPoints();
    }

    /**
     * Gives the exact bettor 10 Points on another Game, so their total is 10 before anything happens on {@code game}.
     */
    private void scoreExactUserOnOtherGame() {
        Game other = createOtherGame();
        Bet otherBet = new Bet();
        otherBet.setUser(exactUser);
        otherBet.setGame(other);
        otherBet.setScoreTeamHome(3);
        otherBet.setScoreTeamGuest(0);
        otherBet.setJoker(1);
        betRepository.save(otherBet);
        enterResult(other, 3, 0);
    }

    @Test
    @DisplayName("uploadResult() - should store each Bet's Points and set each total to the sum of their Bets")
    void shouldStorePointsAndSetTotalsOnEntry() {
        enterResult(2, 1);

        assertEquals(10, storedPoints(exactBet));
        assertEquals(10, storedPoints(winnerBet)); // winner (5) with a x2 Joker
        assertEquals(0, storedPoints(missBet));
        assertEquals(10, total(exactUser));
        assertEquals(10, total(winnerUser));
        assertEquals(0, total(missUser));
    }

    @Test
    @DisplayName("uploadResult() - should sum a user's total over all of their Bets")
    void shouldSumTotalOverAllBetsOfAUser() {
        Game other = createOtherGame();

        Bet otherBet = new Bet();
        otherBet.setUser(exactUser);
        otherBet.setGame(other);
        otherBet.setScoreTeamHome(1);
        otherBet.setScoreTeamGuest(1);
        otherBet.setJoker(1);
        betRepository.save(otherBet);

        ResultReq otherResult = new ResultReq();
        otherResult.setScoreTeamHome(1);
        otherResult.setScoreTeamGuest(1);
        service.uploadResult(other.getId(), otherResult);
        enterResult(2, 1);

        assertEquals(20, total(exactUser));
    }

    @Test
    @DisplayName("uploadResult() - should replace the Points when the Result is corrected, without double counting")
    void shouldReplacePointsOnCorrection() {
        enterResult(2, 1);
        enterResult(1, 0);

        assertEquals(5, storedPoints(exactBet)); // winner only
        assertEquals(20, storedPoints(winnerBet)); // exact 1-0 is 10 Points, x2 Joker
        assertEquals(2, storedPoints(missBet)); // 0-0 still has the guest goals right
        assertEquals(5, total(exactUser));
        assertEquals(20, total(winnerUser));
        assertEquals(2, total(missUser));

        enterResult(1, 0);
        assertEquals(5, total(exactUser));
    }

    @Test
    @DisplayName("uploadResult() - should clear the Points and lower the totals when the Result is removed")
    void shouldClearPointsOnRemoval() {
        enterResult(2, 1);
        enterResult(null, null);

        assertNull(storedPoints(exactBet));
        assertNull(storedPoints(winnerBet));
        assertNull(storedPoints(missBet));
        assertEquals(0, total(exactUser));
        assertEquals(0, total(winnerUser));
    }

    @Test
    @DisplayName("uploadResult() - should clear every Bet's Points when the Game's first Bet was updated after the Result was created")
    void shouldScoreEveryBetWhenABetWasUpdatedAfterTheResult() {
        enterResult(2, 1);

        // The old loop stopped at the first Bet updated after the Result's creation time,
        // so a late Bet sitting first in the Game's Bets left every Bet's Points in the totals on removal.
        Bet lateBet = firstBetOfGame();
        LocalDateTime resultCreatedAt = transaction.execute(status ->
                gameRepository.findById(game.getId()).orElseThrow().getResult().getCreatedAt());
        jdbcTemplate.update("update bet set updated_at = ? where id = ?", resultCreatedAt.plusMinutes(1), lateBet.getId());
        assertTrue(betRepository.findById(lateBet.getId()).orElseThrow().getUpdatedAt().isAfter(resultCreatedAt));
        assertEquals(lateBet.getId(), firstBetOfGame().getId());

        enterResult(null, null);

        assertNull(storedPoints(exactBet));
        assertNull(storedPoints(winnerBet));
        assertNull(storedPoints(missBet));
        assertEquals(0, total(exactUser));
        assertEquals(0, total(winnerUser));
        assertEquals(0, total(missUser));
    }

    /**
     * The first of the Game's Bets in the order result entry iterates them (the same lookup it uses).
     */
    private Bet firstBetOfGame() {
        return transaction.execute(status -> gameRepository.findById(game.getId()).orElseThrow().getBets().getFirst());
    }

    @Test
    @DisplayName("uploadResult() - should leave the last reviewed Points alone for a blank row on a Game without Result")
    void shouldKeepLastReviewedPointsOnBlankRowWithoutResult() {
        scoreExactUserOnOtherGame(); // total 10, last reviewed 0

        enterResult(null, null);

        assertEquals(0, lastReviewed(exactUser));
        assertEquals(10, total(exactUser));
    }

    @Test
    @DisplayName("uploadResult() - should remember the totals before a new Result as the last reviewed Points")
    void shouldSetLastReviewedPointsToTotalsBeforeEntry() {
        scoreExactUserOnOtherGame();

        enterResult(2, 1);

        assertEquals(10, lastReviewed(exactUser));
        assertEquals(20, total(exactUser));
        assertEquals(0, lastReviewed(winnerUser));
    }

    @Test
    @DisplayName("uploadResult() - should remember the totals before the removal as the last reviewed Points")
    void shouldSetLastReviewedPointsToTotalsBeforeRemoval() {
        scoreExactUserOnOtherGame();
        enterResult(2, 1);

        enterResult(null, null);

        assertEquals(20, lastReviewed(exactUser));
        assertEquals(10, total(exactUser));
        assertEquals(10, lastReviewed(winnerUser));
    }

    @Test
    @DisplayName("uploadResult() - should remember the totals without the old Result as the last reviewed Points on a correction")
    void shouldSetLastReviewedPointsToTotalsWithoutOldResultOnCorrection() {
        scoreExactUserOnOtherGame();
        enterResult(2, 1); // exact bettor 20, winner bettor 10

        enterResult(1, 0); // exact bettor 15, winner bettor 20

        assertEquals(10, lastReviewed(exactUser));
        assertEquals(15, total(exactUser));
        assertEquals(0, lastReviewed(winnerUser));
        assertEquals(20, total(winnerUser));
    }
}
