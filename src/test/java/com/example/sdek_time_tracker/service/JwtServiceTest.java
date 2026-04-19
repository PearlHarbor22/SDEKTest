package com.example.sdek_time_tracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0c2U="
        );
        ReflectionTestUtils.setField(jwtService, "expiration", 3600000L);
    }

    @Test
    void generateToken_shouldReturnNonEmptyToken() {
        String token = jwtService.generateToken("admin");

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_shouldReturnUsernameFromToken() {
        String token = jwtService.generateToken("admin");

        String username = jwtService.extractUsername(token);

        assertEquals("admin", username);
    }

    @Test
    void isTokenValid_shouldReturnTrue_whenTokenBelongsToUsernameAndNotExpired() {
        String token = jwtService.generateToken("admin");

        boolean isValid = jwtService.isTokenValid(token, "admin");

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenBelongsToAnotherUsername() {
        String token = jwtService.generateToken("admin");

        boolean isValid = jwtService.isTokenValid(token, "user");

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_shouldReturnFalse_whenTokenIsExpired() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "expiration", 1L);

        String token = jwtService.generateToken("admin");
        Thread.sleep(10);

        boolean isValid = jwtService.isTokenValid(token, "admin");

        assertFalse(isValid);
    }
}