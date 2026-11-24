package ch.no1hardy.service.model.game;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The goals of the home and the guest team. Shared by Bet and Result (Score entity).
 */
@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Scoreline {
    @NotNull
    private Integer home;

    @NotNull
    private Integer guest;

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
