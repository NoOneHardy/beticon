package ch.no1hardy.service.model;

import ch.no1hardy.service.model.game.Score;
import ch.no1hardy.service.model.game.Scoreline;
import ch.no1hardy.service.model.game.ScoreRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ScorelinePersistenceTest {
    @Autowired
    private ScoreRepository repository;

    /** Saves the Result and returns the property paths the persistence layer rejected it for. */
    private Set<String> rejectedPaths(Score result) {
        Throwable cause = assertThrows(RuntimeException.class, () -> repository.save(result));
        while (cause != null && !(cause instanceof ConstraintViolationException)) cause = cause.getCause();
        assertNotNull(cause, "expected the save to be rejected by bean validation");

        return ((ConstraintViolationException) cause).getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("save() - should reject a Result without guest goals")
    void shouldRejectResultWithoutGuestGoals() {
        Score result = new Score();
        result.setScoreline(new Scoreline(2, null));

        assertTrue(rejectedPaths(result).contains("scoreline.guest"));
    }
}
