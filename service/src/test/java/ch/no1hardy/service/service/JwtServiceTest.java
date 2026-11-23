package ch.no1hardy.service.service;

import ch.no1hardy.service.model.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private static final String SECRET_KEY = Base64.getEncoder()
            .encodeToString("jwt-service-test-secret-key-with-at-least-256-bits".getBytes());
    private static final String OTHER_SECRET_KEY = Base64.getEncoder()
            .encodeToString("another-jwt-service-test-secret-key-with-256-bits".getBytes());

    private JwtService service;

    private static JwtService createService(String secretKey, long expiration) {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        jwtService.setJwtExpiration(expiration);
        return jwtService;
    }

    @BeforeEach
    void setUp() {
        service = createService(SECRET_KEY, 3600000L);
    }

    @Test
    @DisplayName("generateToken(String id) - should create a token whose subject is the user id")
    void generateToken01() {
        String token = service.generateToken("user-1");

        assertEquals("user-1", service.extractId(token));
        assertFalse(service.isTokenExpired(token));
    }

    @Test
    @DisplayName("generateToken(Map, String id) - should include extra claims")
    void generateToken02() {
        String token = service.generateToken(Map.of("role", "admin"), "user-1");

        assertEquals("admin", service.extractClaim(token, claims -> claims.get("role", String.class)));
    }

    @Test
    @DisplayName("isTokenValid(String token, UserDetails) - should only accept the token owner")
    void isTokenValid01() {
        String token = service.generateToken("user-1");
        User owner = new User();
        owner.setId("user-1");
        User other = new User();
        other.setId("user-2");

        assertTrue(service.isTokenValid(token, owner));
        assertFalse(service.isTokenValid(token, other));
    }

    @Test
    @DisplayName("extractId(String token) - should reject tokens signed with another key")
    void extractId01() {
        String foreignToken = createService(OTHER_SECRET_KEY, 3600000L).generateToken("user-1");

        assertThrows(SignatureException.class, () -> service.extractId(foreignToken));
    }

    @Test
    @DisplayName("extractId(String token) - should reject expired tokens")
    void extractId02() {
        String expiredToken = service.buildToken(Map.of(), "user-1", -1000L);

        assertThrows(ExpiredJwtException.class, () -> service.extractId(expiredToken));
    }

    @Test
    @DisplayName("generateJwtCookie(String id) - should create a secure http-only cookie with the token")
    void generateJwtCookie01() {
        Cookie cookie = service.generateJwtCookie("user-1");

        assertEquals("jwt", cookie.getName());
        assertTrue(cookie.getSecure());
        assertTrue(cookie.isHttpOnly());
        assertEquals("/", cookie.getPath());
        assertEquals(3600, cookie.getMaxAge());
        assertEquals("user-1", service.extractId(cookie.getValue()));
    }
}
