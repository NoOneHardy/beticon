package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.game.BetReq;
import ch.no1hardy.service.front.game.ResultReq;
import ch.no1hardy.service.front.group.GroupReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GroupControllerTest extends ApiTestBase {
    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /group - parses the create group request sent by the admin frontend")
    void parsesCreateGroupRequest() throws Exception {
        mockMvc.perform(post("/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Gruppe A", "isKnockout": true, "order": 3, "thumbnail": null}
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<GroupReq> captor = ArgumentCaptor.forClass(GroupReq.class);
        verify(groupService).create(captor.capture());
        GroupReq forwarded = captor.getValue();
        assertEquals("Gruppe A", forwarded.getName());
        assertEquals(true, forwarded.getIsKnockout());
        assertEquals(3, forwarded.getOrder());
        assertNull(forwarded.getThumbnail());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /group/{id}/bets - parses the list of bets sent by the group view")
    void parsesBetListRequest() throws Exception {
        mockMvc.perform(put("/group/group-a/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"game": "game-1", "scoreTeamHome": 1, "scoreTeamGuest": 1, "joker": 3},
                                  {"game": "game-2", "scoreTeamHome": null, "scoreTeamGuest": null, "joker": 1}
                                ]
                                """))
                .andExpect(status().isOk());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BetReq>> captor = ArgumentCaptor.forClass(List.class);
        verify(groupService).updateBets(eq("group-a"), captor.capture());
        List<BetReq> forwarded = captor.getValue();
        assertEquals(2, forwarded.size());
        assertEquals("game-1", forwarded.get(0).getGame());
        assertEquals(1, forwarded.get(0).getScoreTeamHome());
        assertEquals(1, forwarded.get(0).getScoreTeamGuest());
        assertEquals(3, forwarded.get(0).getJoker());
        assertEquals("game-2", forwarded.get(1).getGame());
        assertNull(forwarded.get(1).getScoreTeamHome());
        assertNull(forwarded.get(1).getScoreTeamGuest());
        assertEquals(1, forwarded.get(1).getJoker());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /group/{id}/results - parses the list of results sent by the group view")
    void parsesResultListRequest() throws Exception {
        mockMvc.perform(put("/group/group-a/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"game": "game-1", "scoreTeamHome": 4, "scoreTeamGuest": 2}]
                                """))
                .andExpect(status().isOk());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ResultReq>> captor = ArgumentCaptor.forClass(List.class);
        verify(groupService).updateResults(eq("group-a"), captor.capture());
        List<ResultReq> forwarded = captor.getValue();
        assertEquals(1, forwarded.size());
        assertEquals("game-1", forwarded.getFirst().getGame());
        assertEquals(4, forwarded.getFirst().getScoreTeamHome());
        assertEquals(2, forwarded.getFirst().getScoreTeamGuest());
    }
}
