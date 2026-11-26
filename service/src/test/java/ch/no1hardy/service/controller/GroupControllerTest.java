package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.game.BetReq;
import ch.no1hardy.service.front.game.ResultReq;
import ch.no1hardy.service.front.group.GroupReq;
import ch.no1hardy.service.front.group.GroupRes;
import ch.no1hardy.service.front.group.JokerQuota;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GroupControllerTest extends ApiTestBase {
    @Test
    @WithMockUser
    @DisplayName("GET /group/{id} - sends the joker quota under the glossary property names")
    void sendsJokerQuotaPropertyNames() throws Exception {
        JokerQuota jokerQuota = new JokerQuota(2, 1, 10, 6);
        when(groupService.getGroup("group-a")).thenReturn(
                new GroupRes(
                        "group-a",
                        null,
                        null,
                        null,
                        null,
                        null,
                        jokerQuota,
                        null,
                        null
                )
        );

        mockMvc.perform(get("/group/group-a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.jokerQuota.remainingDoubleJokers").value(2))
                .andExpect(jsonPath("$.data.jokerQuota.remainingTripleJokers").value(1))
                .andExpect(jsonPath("$.data.jokerQuota.doubleJokerLimit").value(10))
                .andExpect(jsonPath("$.data.jokerQuota.tripleJokerLimit").value(6))
                .andExpect(jsonPath("$.data.availableJokers").doesNotExist());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /group - parses the create group request sent by the admin frontend")
    void parsesCreateGroupRequest() throws Exception {
        mockMvc.perform(
                post("/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Gruppe A", "isKnockout": true, "order": 3, "thumbnail": null}
                                """)
        )
                .andExpect(status().isOk());

        ArgumentCaptor<GroupReq> captor = ArgumentCaptor.forClass(GroupReq.class);
        verify(groupService).create(captor.capture());
        GroupReq forwarded = captor.getValue();
        assertEquals("Gruppe A", forwarded.name());
        assertEquals(true, forwarded.isKnockout());
        assertEquals(3, forwarded.order());
        assertNull(forwarded.thumbnail());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /group/{id}/bets - parses the list of bets sent by the group view")
    void parsesBetListRequest() throws Exception {
        mockMvc.perform(
                put("/group/group-a/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"game": "game-1", "scoreTeamHome": 1, "scoreTeamGuest": 1, "joker": 3},
                                  {"game": "game-2", "scoreTeamHome": null, "scoreTeamGuest": null, "joker": 1}
                                ]
                                """)
        )
                .andExpect(status().isOk());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BetReq>> captor = ArgumentCaptor.forClass(List.class);
        verify(groupService).updateBets(eq("group-a"), captor.capture());
        List<BetReq> forwarded = captor.getValue();
        assertEquals(2, forwarded.size());
        assertEquals("game-1", forwarded.get(0).game());
        assertEquals(1, forwarded.get(0).scoreTeamHome());
        assertEquals(1, forwarded.get(0).scoreTeamGuest());
        assertEquals(3, forwarded.get(0).joker());
        assertEquals("game-2", forwarded.get(1).game());
        assertNull(forwarded.get(1).scoreTeamHome());
        assertNull(forwarded.get(1).scoreTeamGuest());
        assertEquals(1, forwarded.get(1).joker());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /group/{id}/results - parses the list of results sent by the group view")
    void parsesResultListRequest() throws Exception {
        mockMvc.perform(
                put("/group/group-a/results")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [{"game": "game-1", "scoreTeamHome": 4, "scoreTeamGuest": 2}]
                                """)
        )
                .andExpect(status().isOk());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ResultReq>> captor = ArgumentCaptor.forClass(List.class);
        verify(groupService).updateResults(eq("group-a"), captor.capture());
        List<ResultReq> forwarded = captor.getValue();
        assertEquals(1, forwarded.size());
        assertEquals("game-1", forwarded.getFirst().game());
        assertEquals(4, forwarded.getFirst().scoreTeamHome());
        assertEquals(2, forwarded.getFirst().scoreTeamGuest());
    }
}
