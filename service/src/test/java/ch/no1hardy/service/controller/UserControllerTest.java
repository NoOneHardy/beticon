package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.user.UserReq;
import ch.no1hardy.service.front.user.UserRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends ApiTestBase {
    @Test
    @WithMockUser
    @DisplayName("PUT /user/me - should update current user when authenticated")
    void shouldUpdateCurrentUserWhenAuthenticated() throws Exception {
        UserReq dto = new UserReq();
        dto.setUsername("No1HardyUpdated");
        dto.setEmail("silas@test.ch");
        dto.setFirstname("SilasUpdated");
        dto.setLastname("HardyUpdated");
        dto.setPassword("newsecurepassword");

        UserRes res = new UserRes();
        res.setId("user-id-123");
        res.setUsername("No1HardyUpdated");
        res.setFirstname("SilasUpdated");
        res.setLastname("HardyUpdated");

        when(userService.update(ArgumentMatchers.any(UserReq.class))).thenReturn(res);

        mockMvc.perform(put("/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("user-id-123"))
                .andExpect(jsonPath("$.data.username").value("No1HardyUpdated"))
                .andExpect(jsonPath("$.data.firstname").value("SilasUpdated"))
                .andExpect(jsonPath("$.data.lastname").value("HardyUpdated"));

        ArgumentCaptor<UserReq> captor = ArgumentCaptor.forClass(UserReq.class);
        Mockito.verify(userService, Mockito.times(1)).update(captor.capture());
        UserReq forwarded = captor.getValue();
        assertEquals("No1HardyUpdated", forwarded.getUsername());
        assertEquals("SilasUpdated", forwarded.getFirstname());
        assertEquals("HardyUpdated", forwarded.getLastname());
        assertEquals("newsecurepassword", forwarded.getPassword());
    }

    @Test
    @DisplayName("PUT /user/me - should reject the request when no user is authenticated")
    void shouldRejectWhenNotAuthenticated() throws Exception {
        UserReq dto = new UserReq();
        dto.setUsername("No1HardyUpdated");

        mockMvc.perform(put("/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());

        Mockito.verify(userService, Mockito.never()).update(ArgumentMatchers.any(UserReq.class));
    }
}
