package ch.no1hardy.service.service;

import ch.no1hardy.service.exception.user.UserNotFoundException;
import ch.no1hardy.service.front.user.UserRes;
import ch.no1hardy.service.mapper.UserMapperImpl;
import ch.no1hardy.service.model.game.BetRepository;
import ch.no1hardy.service.model.user.User;
import ch.no1hardy.service.model.user.UserApplicationStatus;
import ch.no1hardy.service.model.user.UserRepository;
import ch.no1hardy.service.front.admin.PointsChangeRes;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
public class AdminService {
    private UserService userService;
    private NotificationService notificationService;
    private UserMapperImpl userMapper;
    private UserRepository userRepository;
    private BetRepository betRepository;
    private PointsService pointsService;

    /**
     * Confirms a user by their ID.
     * @param id the ID of the user to confirm
     * @return the confirmed user as a UserRes object
     * @throws UserNotFoundException if the user with the given ID does not exist
     */
    public UserRes approveUser(@NotNull String id) throws UserNotFoundException {
        User user = userService.getRaw(id);
        if (user.isApproved()) return userMapper.toDto(user);

        user.approve();
        notificationService.notifyUserApproval(user);
        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Denies a user by their ID.
     * @param id the ID of the user to deny
     * @return the denied user as a UserRes object
     * @throws UserNotFoundException if the user with the given ID does not exist
     */
    public UserRes denyUser(String id) throws UserNotFoundException {
        User user = userService.getRaw(id);
        if (user.getUserApplicationStatus() == UserApplicationStatus.DENIED) return userMapper.toDto(user);

        user.deny();
        notificationService.notifyUserRejection(user);
        return userMapper.toDto(userRepository.save(user));
    }

    /**
     * Rebuilds every Bet's Points and every user's total from Bets and Results (see ADR-0001).
     *
     * @return the users whose total changed, with the old and the new value
     */
    @Transactional
    public List<PointsChangeRes> rescoreEverything() {
        List<User> users = userRepository.findAll();
        Map<String, Integer> oldTotals = new HashMap<>();
        users.forEach(user -> oldTotals.put(user.getId(), user.getPoints()));

        pointsService.rescoreBets(betRepository.findAll());
        pointsService.recomputeTotals(users);

        List<PointsChangeRes> changes = users.stream()
                .filter(user -> !Objects.equals(oldTotals.get(user.getId()), user.getPoints()))
                .map(user -> PointsChangeRes.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .oldPoints(oldTotals.get(user.getId()))
                        .newPoints(user.getPoints())
                        .build())
                .toList();
        changes.forEach(change -> log.info("Re-scored user {}: {} -> {} Points",
                change.getUsername(), change.getOldPoints(), change.getNewPoints()));
        log.info("Re-scored everything: {} of {} user totals changed", changes.size(), users.size());
        return changes;
    }
}
