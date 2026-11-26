package ch.no1hardy.service.front;

import ch.no1hardy.service.front.game.PreviousGameRes;
import ch.no1hardy.service.front.team.ExtendedTeamRes;
import ch.no1hardy.service.front.team.TeamRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtendedTeamResTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("should serialize the team fields next to the previous games")
    void shouldSerializeFlat() {
        TeamRes team = new TeamRes("team-1", "Schweiz", "SUI", "ch");
        PreviousGameRes previous = new PreviousGameRes("game-1", team, team, null, "Gruppe A");
        ExtendedTeamRes extended = new ExtendedTeamRes(team, List.of(previous));

        JsonNode json = objectMapper.valueToTree(extended);

        assertEquals(List.of("id", "name", "shortName", "flag", "previousGames"), List.copyOf(json.propertyNames()));
        assertEquals("Schweiz", json.get("name").asString());
        assertEquals("game-1", json.get("previousGames").get(0).get("id").asString());
    }
}
