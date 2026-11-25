package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.preferences.NotificationPreferenceReq;
import ch.no1hardy.service.model.notification.Channel;
import ch.no1hardy.service.model.notification.NotificationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PreferencesControllerTest extends ApiTestBase {
    @Test
    @WithMockUser
    @DisplayName("PUT /preferences/notifications - parses the notification preferences sent by the settings page")
    void parsesNotificationPreferencesRequest() throws Exception {
        mockMvc.perform(put("/preferences/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {"type": "NEW_GAME", "channel": "EMAIL", "selected": true},
                                  {"type": "NEW_RESULT", "channel": "IN_APP", "selected": false}
                                ]
                                """))
                .andExpect(status().isOk());

        verify(preferencesService).updateNotificationPrefs(List.of(
                new NotificationPreferenceReq(NotificationType.NEW_GAME, Channel.EMAIL, true),
                new NotificationPreferenceReq(NotificationType.NEW_RESULT, Channel.IN_APP, false)
        ));
    }
}
