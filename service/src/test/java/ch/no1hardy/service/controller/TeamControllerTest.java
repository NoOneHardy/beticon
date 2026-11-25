package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.team.TeamReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TeamControllerTest extends ApiTestBase {
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /team - parses the create team request sent by the admin frontend")
    void parsesCreateTeamRequest() throws Exception {
        mockMvc.perform(post("/team")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Schweiz", "shortName": "SUI", "flag": "flags/sui.png"}
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<TeamReq> captor = ArgumentCaptor.forClass(TeamReq.class);
        verify(teamService).create(captor.capture());
        TeamReq forwarded = captor.getValue();
        assertEquals("Schweiz", forwarded.getName());
        assertEquals("SUI", forwarded.getShortName());
        assertEquals("flags/sui.png", forwarded.getFlag());
    }
}
