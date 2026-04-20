package com.example.sdek_time_tracker.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Сервис для генерации и валидации JWT токенов.
 */
@Service
public class JwtService {

    @Value("${app.security.jwt.secret}")
    private String secret;

    @Value("${app.security.jwt.expiration}")
    private long expiration;

    /**
     * Генерирует JWT токен для указанного пользователя.
     *
     * @param username username, который будет записан в subject токена
     * @return сгенерированный JWT токен
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Извлекает username из JWT токена.
     *
     * @param token JWT токен
     * @return username из subject токена
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Проверяет, что токен принадлежит указанному пользователю и не истек.
     *
     * @param token JWT токен
     * @param username ожидаемый username
     * @return true, если токен валиден, иначе false
     */
    public boolean isTokenValid(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);
            return extractedUsername.equals(username) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            // Для невалидного или поврежденного токена возвращаем false
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
