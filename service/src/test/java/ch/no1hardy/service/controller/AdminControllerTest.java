package ch.no1hardy.service.controller;

import ch.no1hardy.service.front.admin.PointsChangeRes;
import ch.no1hardy.service.service.AdminService;
import ch.no1hardy.service.service.AuthService;
import ch.no1hardy.service.service.JwtService;
import ch.no1hardy.service.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
    @TestConfiguration
    static class SecurityMockMvcCustomizerConfig {
        @Bean
        MockMvcBuilderCustomizer securityMockMvcBuilderCustomizer() {
            return builder -> builder.apply(SecurityMockMvcConfigurers.springSecurity());
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthService authService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /admin/rescore - should return the changed totals for an admin")
    void shouldRescoreForAdmin() throws Exception {
        PointsChangeRes change = PointsChangeRes.builder()
                .userId("u1").username("hardy").oldPoints(3).newPoints(8).build();
        when(adminService.rescoreEverything()).thenReturn(List.of(change));

        mockMvc.perform(post("/admin/rescore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value("u1"))
                .andExpect(jsonPath("$.data[0].username").value("hardy"))
                .andExpect(jsonPath("$.data[0].oldPoints").value(3))
                .andExpect(jsonPath("$.data[0].newPoints").value(8));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /admin/rescore - should refuse a non-admin")
    void shouldRefuseNonAdmin() throws Exception {
        mockMvc.perform(post("/admin/rescore")).andExpect(status().is4xxClientError());

        verify(adminService, never()).rescoreEverything();
    }
}
