package ch.no1hardy.service.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MalformedRequestTest extends ApiTestBase {
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("a request body that is not valid JSON is rejected as a bad request")
    void rejectsInvalidJson() throws Exception {
        mockMvc.perform(
                post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Schweiz\",")
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request body"))
                .andExpect(jsonPath("$.displayMessage").value("Ungültige Anfrage"))
                .andExpect(jsonPath("$.status").value(400));

        verify(teamService, never()).create(any());
    }

    @Test
    @WithMockUser
    @DisplayName("a request body with a value of the wrong type is rejected as a bad request")
    void rejectsTypeMismatch() throws Exception {
        mockMvc.perform(
                put("/game/game-1/bet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"game": "game-1", "scoreTeamHome": "two", "scoreTeamGuest": 1, "joker": 1}
                                """)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request body"))
                .andExpect(jsonPath("$.status").value(400));

        verify(gameService, never()).uploadBet(any(), any());
    }
}
