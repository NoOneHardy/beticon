package ch.no1hardy.service.model.game;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

/**
 * The goals of the home and the guest team. Shared by Bet and Result (Score entity).
 */
@Embeddable
public record Scoreline(@NotNull Integer home, @NotNull Integer guest) {
    public boolean homeWins() {
        return home > guest;
    }

    public boolean guestWins() {
        return guest > home;
    }

    public boolean isTie() {
        return home.equals(guest);
    }

    public int total() {
        return home + guest;
    }
}
