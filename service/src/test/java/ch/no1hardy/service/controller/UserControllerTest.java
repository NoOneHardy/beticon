package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.user.LoginReq;
import ch.no1hardy.service.front.user.UserReq;
import ch.no1hardy.service.front.user.UserRes;
import ch.no1hardy.service.front.verification.ResetPasswordReq;
import ch.no1hardy.service.front.verification.VerifyEmailReq;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @DisplayName("POST /signup - parses the sign-up request")
    void parsesSignupRequest() throws Exception {
        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "no1hardy",
                                  "email": "hardy@example.com",
                                  "firstname": "Hardy",
                                  "lastname": "Tester",
                                  "password": "secret-password"
                                }
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<UserReq> captor = ArgumentCaptor.forClass(UserReq.class);
        verify(userService).create(captor.capture());
        UserReq forwarded = captor.getValue();
        assertEquals("no1hardy", forwarded.getUsername());
        assertEquals("hardy@example.com", forwarded.getEmail());
        assertEquals("Hardy", forwarded.getFirstname());
        assertEquals("Tester", forwarded.getLastname());
        assertEquals("secret-password", forwarded.getPassword());
    }

    @Test
    @DisplayName("POST /login - parses the login request")
    void parsesLoginRequest() throws Exception {
        UserRes user = new UserRes();
        user.setId("user-1");
        when(userService.login(any(LoginReq.class))).thenReturn(user);
        when(jwtService.generateJwtCookie(anyString())).thenReturn(new Cookie("jwt", "token"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "no1hardy", "password": "secret-password"}
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<LoginReq> captor = ArgumentCaptor.forClass(LoginReq.class);
        verify(userService).login(captor.capture());
        assertEquals("no1hardy", captor.getValue().getUsername());
        assertEquals("secret-password", captor.getValue().getPassword());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /user/me - parses the update request sent by the profile page, ignoring its id")
    void parsesProfileUpdateRequest() throws Exception {
        mockMvc.perform(put("/user/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "user-1",
                                  "username": "no1hardy",
                                  "email": "hardy@example.com",
                                  "passwordChange": {"password": "new-password", "currentPassword": "old-password"}
                                }
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<UserReq> captor = ArgumentCaptor.forClass(UserReq.class);
        verify(userService).update(captor.capture());
        UserReq forwarded = captor.getValue();
        assertEquals("no1hardy", forwarded.getUsername());
        assertEquals("hardy@example.com", forwarded.getEmail());
        assertEquals(new UserReq.PasswordChange("new-password", "old-password"), forwarded.getPasswordChange());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /user/{id} - parses the admin update request")
    void parsesAdminUpdateRequest() throws Exception {
        mockMvc.perform(put("/user/user-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "no1hardy", "firstname": "Hardy", "lastname": "Tester"}
                                """))
                .andExpect(status().isOk());

        ArgumentCaptor<UserReq> captor = ArgumentCaptor.forClass(UserReq.class);
        verify(userService).update(eq("user-1"), captor.capture());
        UserReq forwarded = captor.getValue();
        assertEquals("no1hardy", forwarded.getUsername());
        assertEquals("Hardy", forwarded.getFirstname());
        assertEquals("Tester", forwarded.getLastname());
    }

    @Test
    @DisplayName("POST /user/confirm - parses the email verification request")
    void parsesVerifyEmailRequest() throws Exception {
        mockMvc.perform(post("/user/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"code": "verify-code"}
                                """))
                .andExpect(status().isOk());

        verify(userService).verifyEmail(new VerifyEmailReq("verify-code"));
    }

    @Test
    @DisplayName("PUT /user/password-reset - parses the password reset request")
    void parsesResetPasswordRequest() throws Exception {
        mockMvc.perform(put("/user/password-reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"newPassword": "new-password", "code": "reset-code"}
                                """))
                .andExpect(status().isOk());

        verify(userService).resetPassword(new ResetPasswordReq("reset-code", "new-password"));
    }
}
