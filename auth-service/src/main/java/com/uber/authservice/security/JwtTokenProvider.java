package com.uber.authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-expiration-ms}") long accessExp,
            @Value("${app.jwt.refresh-expiration-ms}") long refreshExp) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMs = accessExp;
        this.refreshExpirationMs = refreshExp;
    }


    public String generateAccessToken(Long userId, String email, String role) {
        return buildToken(email, Map.of("userId", String.valueOf(userId), "role", role), accessExpirationMs);
    }

    public String generateRefreshToken(String email) {
        return buildToken(email, Map.of(), refreshExpirationMs);
    }

    private String buildToken(String subject, Map<String, String> claims, long expirationMs) {
        Date now = new Date();
        var builder = Jwts.builder().subject(subject).issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs));
        claims.forEach(builder::claim);
        return builder.signWith(key).compact();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try { 
            parseClaims(token);
            return true; 
        }
        catch (JwtException | IllegalArgumentException e) { 
            return false; 
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
