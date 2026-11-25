package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.game.GameReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerTest extends ApiTestBase {
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /game - parses the create game request sent by the admin frontend")
    void parsesCreateGameRequest() throws Exception {
        mockMvc.perform(post("/game")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "timestamp": "2026-06-11T16:00:00.000Z",
                                  "group": "group-a",
                                  "teamHome": "team-sui",
                                  "teamGuest": "team-ger"
                                }
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<GameReq> captor = ArgumentCaptor.forClass(GameReq.class);
        verify(gameService).create(captor.capture());
        GameReq forwarded = captor.getValue();
        assertEquals(LocalDateTime.of(2026, 6, 11, 16, 0), forwarded.getTimestamp());
        assertEquals("group-a", forwarded.getGroup());
        assertEquals("team-sui", forwarded.getTeamHome());
        assertEquals("team-ger", forwarded.getTeamGuest());
    }
}
