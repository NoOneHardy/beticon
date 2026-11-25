package ch.no1hardy.service.controller;

import ch.no1hardy.service.service.AdminService;
import ch.no1hardy.service.service.AuthService;
import ch.no1hardy.service.service.DashboardService;
import ch.no1hardy.service.config.ApiResponseAdvice;
import ch.no1hardy.service.config.SecurityConfiguration;
import ch.no1hardy.service.exception.GlobalExceptionHandler;
import ch.no1hardy.service.service.GameService;
import ch.no1hardy.service.service.GlobalDataService;
import ch.no1hardy.service.service.GroupService;
import ch.no1hardy.service.service.JwtService;
import ch.no1hardy.service.service.LeaderboardService;
import ch.no1hardy.service.service.NotificationService;
import ch.no1hardy.service.service.PreferencesService;
import ch.no1hardy.service.service.TeamService;
import ch.no1hardy.service.service.UserService;
import ch.no1hardy.service.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * Base for API tests: drives the real HTTP layer (routing, security, JSON parsing, response envelope)
 * through a @WebMvcTest MockMvc slice while every service is mocked. All API tests share one Spring context because they
 * declare the same mocks here.
 */
@WebMvcTest
@Import({SecurityConfiguration.class, GlobalExceptionHandler.class, ApiResponseAdvice.class})
public abstract class ApiTestBase {
    @TestConfiguration
    static class SecurityMockMvcCustomizerConfig {
        @Bean
        MockMvcBuilderCustomizer securityMockMvcBuilderCustomizer() {
            return builder -> builder.apply(SecurityMockMvcConfigurers.springSecurity());
        }
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected NotificationService notificationService;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected GameService gameService;

    @MockitoBean
    protected GroupService groupService;

    @MockitoBean
    protected TeamService teamService;

    @MockitoBean
    protected PreferencesService preferencesService;

    @MockitoBean
    protected AdminService adminService;

    @MockitoBean
    protected DashboardService dashboardService;

    @MockitoBean
    protected LeaderboardService leaderboardService;

    @MockitoBean
    protected StorageService storageService;

    @MockitoBean
    protected GlobalDataService globalDataService;

    @MockitoBean
    protected AuthenticationProvider authenticationProvider;

    @MockitoBean
    protected UserDetailsService userDetailsService;
}
