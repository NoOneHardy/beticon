package ch.no1hardy.service.service;

import ch.no1hardy.service.model.game.Bet;
import ch.no1hardy.service.model.game.BetRepository;
import ch.no1hardy.service.model.game.PointsBreakdown;
import ch.no1hardy.service.model.user.User;
import ch.no1hardy.service.model.user.UserRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Stores Points on Bets and rebuilds user totals from them (see ADR-0001).
 * The scoring rules themselves live in {@code Scoring}; this service only persists their outcome.
 */
@Service
@RequiredArgsConstructor
public class PointsService {
    private final BetRepository betRepository;
    private final UserRepository userRepository;

    /**
     * Scores each Bet against its Game's current Result and stores the total on the Bet.
     * A Bet whose Game has no Result gets its Points cleared (null).
     *
     * @param bets the Bets to re-score
     */
    @Transactional
    public void rescoreBets(@NotNull Collection<Bet> bets) {
        bets.forEach(bet -> bet.setPoints(bet.pointsBreakdown().map(PointsBreakdown::total).orElse(null)));
        betRepository.saveAll(bets);
    }

    /**
     * Sets each user's total to the sum of the stored Points of their Bets. Never a delta.
     *
     * @param users the users whose totals are rebuilt
     */
    @Transactional
    public void recomputeTotals(@NotNull Collection<User> users) {
        users.forEach(user -> user.setPoints(betRepository.sumPointsByUser(user)));
        userRepository.saveAll(users);
    }

    /**
     * Re-scores the given Bets and rebuilds the totals of the users who placed them.
     *
     * @param bets the Bets to re-score
     */
    @Transactional
    public void rescoreBetsAndTotals(@NotNull Collection<Bet> bets) {
        rescoreBets(bets);
        List<User> users = bets.stream()
                .map(Bet::getUser)
                .collect(Collectors.toMap(User::getId, Function.identity(), (a, b) -> a))
                .values().stream().toList();
        recomputeTotals(users);
    }

    /**
     * Remembers every active user's current total as their last reviewed Points,
     * the base the leaderboard's ranking change is measured against.
     * Called right before a Result's Points are removed or added.
     */
    @Transactional
    public void snapshotLastReviewedPoints() {
        List<User> users = userRepository.findAll().stream().filter(User::isActive).toList();
        users.forEach(user -> user.setLastReviewedPoints(user.getPoints()));
        userRepository.saveAll(users);
    }
}
