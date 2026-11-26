package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.game.BetReq;
import ch.no1hardy.service.front.game.GameReq;
import ch.no1hardy.service.front.game.ResultReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerTest extends ApiTestBase {
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /game - parses the create game request sent by the admin frontend")
    void parsesCreateGameRequest() throws Exception {
        mockMvc.perform(
                post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "timestamp": "2026-06-11T16:00:00.000Z",
                                  "group": "group-a",
                                  "teamHome": "team-sui",
                                  "teamGuest": "team-ger"
                                }
                                """)
        )
                .andExpect(status().isOk());

        ArgumentCaptor<GameReq> captor = ArgumentCaptor.forClass(GameReq.class);
        verify(gameService).create(captor.capture());
        GameReq forwarded = captor.getValue();
        assertEquals(LocalDateTime.of(2026, 6, 11, 16, 0), forwarded.timestamp());
        assertEquals("group-a", forwarded.group());
        assertEquals("team-sui", forwarded.teamHome());
        assertEquals("team-ger", forwarded.teamGuest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /game/{id}/result - parses the result request")
    void parsesResultRequest() throws Exception {
        mockMvc.perform(
                put("/game/game-1/result")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"game": "game-1", "scoreTeamHome": 2, "scoreTeamGuest": 1}
                                """)
        )
                .andExpect(status().isOk());

        ArgumentCaptor<ResultReq> captor = ArgumentCaptor.forClass(ResultReq.class);
        verify(gameService).uploadResult(eq("game-1"), captor.capture());
        ResultReq forwarded = captor.getValue();
        assertEquals("game-1", forwarded.game());
        assertEquals(2, forwarded.scoreTeamHome());
        assertEquals(1, forwarded.scoreTeamGuest());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /game/{id}/bet - parses the bet request")
    void parsesBetRequest() throws Exception {
        mockMvc.perform(
                put("/game/game-1/bet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"game": "game-1", "scoreTeamHome": 0, "scoreTeamGuest": 3, "joker": 2}
                                """)
        )
                .andExpect(status().isOk());

        ArgumentCaptor<BetReq> captor = ArgumentCaptor.forClass(BetReq.class);
        verify(gameService).uploadBet(eq("game-1"), captor.capture());
        BetReq forwarded = captor.getValue();
        assertEquals("game-1", forwarded.game());
        assertEquals(0, forwarded.scoreTeamHome());
        assertEquals(3, forwarded.scoreTeamGuest());
        assertEquals(2, forwarded.joker());
    }
}
