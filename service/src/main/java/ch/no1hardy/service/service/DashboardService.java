package ch.no1hardy.service.service;

import ch.no1hardy.service.exception.user.NotLoggedInException;
import ch.no1hardy.service.front.dashboard.DashboardData;
import ch.no1hardy.service.front.dashboard.GlobalStatistics;
import ch.no1hardy.service.front.dashboard.Statistics;
import ch.no1hardy.service.front.dashboard.UserSummary;
import ch.no1hardy.service.front.home.HomeRes;
import ch.no1hardy.service.front.leaderboard.RankingRes;
import ch.no1hardy.service.model.game.Bet;
import ch.no1hardy.service.model.game.PointsBreakdown;
import ch.no1hardy.service.model.user.User;
import ch.no1hardy.service.model.user.UserApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    @Value("${beticon.participation.fee}")
    private Integer participationFee;

    private final AuthService authService;
    private final UserService userService;
    private final GameService gameService;
    private final LeaderboardService leaderboardService;
    private final GroupService groupService;

    /**
     * Retrieves the dashboard data for the logged-in user.
     * @return a DashboardData object containing various statistics and summaries
     * @throws NotLoggedInException if the user is not logged in
     */
    public DashboardData getDashboard() throws NotLoggedInException {
        return new DashboardData(
                leaderboardService.getUserLeaderboard(),
                getUserSummary(),
                getStatistics(),
                getGlobalStatistics(),
                gameService.getUpcomingGames(),
                gameService.getRecentResults()
        );
    }

    /**
     * Retrieves global statistics for all users.
     * @return a GlobalStatistics object containing total points, exact bets, and wasted jokers
     */
    public GlobalStatistics getGlobalStatistics() {
        List<User> users = userService.listRaw();
        return new GlobalStatistics(
                users.stream().mapToInt(User::getPoints).sum(),
                users.stream().mapToInt(this::getExactBets).sum(),
                users.stream().mapToInt(this::getWastedJokers).sum()
        );
    }

    /**
     * Retrieves a summary of the logged-in user's dashboard.
     * @return a UserSummary object containing the user's points, percentage of games bet, confirmation status, and ranking
     * @throws NotLoggedInException if the user is not logged in
     * */
    public UserSummary getUserSummary() throws NotLoggedInException {
        User user = authService.getLoggedInUser().orElseThrow(NotLoggedInException::new);

        Boolean isConfirmed;
        if (user.getUserApplicationStatus() == UserApplicationStatus.ACCEPTED
                && user.getApplicationReviewedAt() != null)
            isConfirmed = true;
        else if (user.getUserApplicationStatus() == UserApplicationStatus.DENIED
                && user.getApplicationReviewedAt() != null)
            isConfirmed = false;
        else
            isConfirmed = null;

        List<RankingRes> userLeaderboard = leaderboardService.getUserLeaderboard(user);
        return new UserSummary(
                user.getPoints(),
                userLeaderboard.size() == 3 ? userLeaderboard.get(1).ranking() : null,
                groupService.getOverallPercentage(user),
                isConfirmed
        );
    }

    /**
     * Calculates statistics for the logged-in user.
     * @return statistics for the logged-in user
     * @throws NotLoggedInException if the user is not logged in
     */
    public Statistics getStatistics() throws NotLoggedInException {
        User user = authService.getLoggedInUser().orElseThrow(NotLoggedInException::new);
        return getStatistics(user);
    }

    /**
     * Calculates statistics for a specific user.
     * @param user the user for whom to calculate statistics
     * @return statistics for the specified user
     */
    public Statistics getStatistics(@NotNull User user) {
        return new Statistics(getTotalGoalsBet(user), getExactBets(user), getWastedJokers(user));
    }

    /**
     * Calculates the total number of goals bet by the user.
     * @param user the user for whom to calculate total goals bet
     * @return total number of goals bet by the user
     */
    public int getTotalGoalsBet(@NotNull User user) {
        return user.getBets().stream()
                .filter(Bet::isActive)
                .mapToInt(bet -> bet.getScoreTeamHome() + bet.getScoreTeamGuest())
                .sum();
    }

    /**
     * Calculates the number of Exact Bets placed by the user.
     * @param user the user for whom to calculate Exact Bets
     * @return number of Exact Bets placed by the user
     */
    public int getExactBets(@NotNull User user) {
        return (int) user.getBets().stream()
                .filter(Bet::isActive)
                .filter(bet -> bet.pointsBreakdown().map(PointsBreakdown::isExact).orElse(false))
                .count();
    }

    /**
     * Calculates the number of wasted jokers of the user.
     * A joker is wasted if it was used on a bet that earned no points; it counts once per bet, whatever its multiplier.
     * @param user the user for whom to calculate wasted jokers
     * @return number of wasted jokers of the user
     */
    public int getWastedJokers(@NotNull User user) {
        return (int) user.getBets().stream()
                .filter(Bet::isActive)
                .filter(bet -> bet.pointsBreakdown().map(PointsBreakdown::isWastedJoker).orElse(false))
                .count();
    }

    /**
     * Get a record of the data that is displayed on the home screen:
     * - jackpot - how much money is in the jackpot
     * - participants - how many players are playing in total
     * - games - how many games are online
     *
     * @return the necessary data for the home screen as record
     */
    public HomeRes getHomeData() {
        return new HomeRes(
                getJackpot(),
                getParticipantsCount(),
                getGamesCount()
        );
    }

    /**
     * Calculate how much money is in the jackpot.
     * The jackpot is the product of the participation fee and the number of approved players.
     * @return an integer representing the amount of money in the jackpot
     */
    public int getJackpot() {
        return getParticipantsCount() * this.participationFee;
    }

    /**
     * How many players are confirmed.
     * @return an integer representing the amonut of confirmed players.
     */
    public int getParticipantsCount() {
        return userService.listConfirmedRaw().size();
    }

    /**
     * How many games are online.
     * @return an integer representing the amount of confirmed players.
     */
    public int getGamesCount() {
        return gameService.getAllActiveGames().size();
    }
}
