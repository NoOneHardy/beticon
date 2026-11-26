package ch.no1hardy.service.model;

import ch.no1hardy.service.model.game.Bet;
import ch.no1hardy.service.model.game.Scoreline;
import ch.no1hardy.service.model.game.Score;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScorelineValidationTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void beforeAll() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void afterAll() {
        factory.close();
    }

    private static <T> Set<String> invalidPaths(T entity) {
        return validator.validate(entity).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("Score - should reject a Result without home goals")
    void shouldRejectResultWithoutHomeGoals() {
        Score result = new Score();
        result.setScoreline(new Scoreline(null, 1));

        assertTrue(invalidPaths(result).contains("scoreline.home"));
    }

    @Test
    @DisplayName("Bet - should reject a Bet without guest goals")
    void shouldRejectBetWithoutGuestGoals() {
        Bet bet = new Bet();
        bet.setScoreline(new Scoreline(1, null));

        assertTrue(invalidPaths(bet).contains("scoreline.guest"));
    }

    @Test
    @DisplayName("Score - should accept a Result with both goals")
    void shouldAcceptResultWithBothGoals() {
        Score result = new Score();
        result.setScoreline(new Scoreline(0, 0));

        assertFalse(invalidPaths(result).stream().anyMatch(path -> path.startsWith("scoreline")));
    }
}
