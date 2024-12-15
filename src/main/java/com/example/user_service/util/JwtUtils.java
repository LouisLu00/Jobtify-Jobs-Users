package com.example.user_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;

public class JwtUtils {

    private static final long EXPIRATION_TIME = 86400000; // 24 hours in milliseconds

    // Generate a JWT Token with only the username
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Set the username as the subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(JwtKeyProvider.getSecretKey(), SignatureAlgorithm.HS512) // Use centralized key
                .compact();
    }

    // Validate a JWT Token and return claims
    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(JwtKeyProvider.getSecretKey()) // Use centralized key
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
