package com.example.user_service.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class JwtKeyProvider {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512); // Generate a secure key
    public static Key getSecretKey() {
        return SECRET_KEY;
    }
}
