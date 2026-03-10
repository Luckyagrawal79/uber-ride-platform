package com.uber.apigateway.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {
    private final SecretKey key;

    public JwtUtil(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public boolean isValid(String token) {
        try { 
            extractClaims(token);
            return true;
        }
        catch (JwtException | IllegalArgumentException e) { return false; }
    }

    public String getEmail(String token) { 
        return extractClaims(token).getSubject(); 
    }

    public String getRole(String token) { 
        return extractClaims(token).get("role", String.class); 
    }
}
