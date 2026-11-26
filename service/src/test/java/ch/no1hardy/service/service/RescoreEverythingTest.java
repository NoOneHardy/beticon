package ch.no1hardy.service.service;

import ch.no1hardy.service.front.admin.PointsChangeRes;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * "Re-score everything" through the admin module's interface, with real repositories and real Scoring.
 * The database is deliberately corrupted first, then rebuilt from Bets and Results.
 */
@SpringBootTest
public class RescoreEverythingTest {
    @Autowired
    private AdminService adminService;
    @Autowired
    private GameService gameService;
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

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private AuthService authService;

    private Game scoredGame;
    private Game openGame;
    private User driftedUser;
    private User untouchedUser;
    private User idleUser;
    private Bet exactBet;
    private Bet openBet;
    private Bet winnerBet;

    @BeforeEach
    void beforeEach() {
        Group group = new Group();
        group.setName("Group " + UUID.randomUUID());
        group = groupRepository.save(group);

        Team home = createTeam();
        Team guest = createTeam();
        scoredGame = createGame(group, home, guest, 1);
        openGame = createGame(group, home, guest, 2);

        driftedUser = createUser();
        untouchedUser = createUser();
        idleUser = createUser();

        exactBet = createBet(driftedUser, scoredGame, 2, 1, 1);
        openBet = createBet(driftedUser, openGame, 1, 1, 1);
        winnerBet = createBet(untouchedUser, scoredGame, 1, 0, 2);

        when(authService.getLoggedInUser()).thenReturn(Optional.of(driftedUser));

        ResultReq result = new ResultReq(null, 2, 1);
        gameService.uploadResult(scoredGame.getId(), result);
    }

    private Team createTeam() {
        Team team = new Team();
        team.setName("Team " + UUID.randomUUID());
        team.setShortName("TST");
        team.setFlag("flag");
        return teamRepository.save(team);
    }

    private Game createGame(Group group, Team home, Team guest, int daysAgo) {
        Game game = new Game();
        game.setGroup(group);
        game.setTimestamp(LocalDateTime.now().minusDays(daysAgo));
        game.setTeamHome(home);
        game.setTeamGuest(guest);
        return gameRepository.save(game);
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

    private Bet createBet(User user, Game game, int home, int guest, int joker) {
        Bet bet = new Bet();
        bet.setUser(user);
        bet.setGame(game);
        bet.setScoreTeamHome(home);
        bet.setScoreTeamGuest(guest);
        bet.setJoker(joker);
        return betRepository.save(bet);
    }

    private void corrupt() {
        setBetPoints(exactBet, null);
        setBetPoints(openBet, 7);
        setBetPoints(winnerBet, 99);
        setTotal(driftedUser, 123);
        setTotal(idleUser, 5);
        // untouchedUser keeps the right total (10) although its Bet Points are wrong
    }

    private void setBetPoints(Bet bet, Integer points) {
        Bet stored = betRepository.findById(bet.getId()).orElseThrow();
        stored.setPoints(points);
        betRepository.save(stored);
    }

    private void setTotal(User user, int points) {
        User stored = userRepository.findById(user.getId()).orElseThrow();
        stored.setPoints(points);
        userRepository.save(stored);
    }

    private Integer storedPoints(Bet bet) {
        return betRepository.findById(bet.getId()).orElseThrow().getPoints();
    }

    private Integer total(User user) {
        return userRepository.findById(user.getId()).orElseThrow().getPoints();
    }

    private PointsChangeRes changeOf(List<PointsChangeRes> changes, User user) {
        return changes.stream().filter(c -> c.userId().equals(user.getId())).findFirst().orElse(null);
    }

    @Test
    @DisplayName("rescoreEverything() - should rebuild Bet Points and user totals from Bets and Results")
    void shouldRebuildPointsAndTotals() {
        corrupt();

        adminService.rescoreEverything();

        assertEquals(10, storedPoints(exactBet));
        assertNull(storedPoints(openBet));
        assertEquals(10, storedPoints(winnerBet));
        assertEquals(10, total(driftedUser));
        assertEquals(10, total(untouchedUser));
        assertEquals(0, total(idleUser));
    }

    @Test
    @DisplayName("rescoreEverything() - should report exactly the users whose total changed, with old and new value")
    void shouldReportChangedTotals() {
        corrupt();

        List<PointsChangeRes> changes = adminService.rescoreEverything();

        PointsChangeRes drifted = changeOf(changes, driftedUser);
        assertEquals(driftedUser.getUsername(), drifted.username());
        assertEquals(123, drifted.oldPoints());
        assertEquals(10, drifted.newPoints());
        PointsChangeRes idle = changeOf(changes, idleUser);
        assertEquals(5, idle.oldPoints());
        assertEquals(0, idle.newPoints());
        assertNull(changeOf(changes, untouchedUser));
    }

    @Test
    @DisplayName("rescoreEverything() - should report no changes on a second run")
    void shouldReportNothingOnSecondRun() {
        corrupt();

        adminService.rescoreEverything();
        List<PointsChangeRes> second = adminService.rescoreEverything();

        assertTrue(second.isEmpty());
        assertEquals(10, total(driftedUser));
    }
}
