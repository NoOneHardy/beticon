package ch.no1hardy.service.front.team;

import ch.no1hardy.service.front.game.PreviousGameRes;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

/**
 * A Team with its previous Games. The Team's fields are serialized next to the previous Games, not nested.
 */
public record ExtendedTeamRes(
        @JsonUnwrapped TeamRes team,
        List<PreviousGameRes> previousGames
) {
}
