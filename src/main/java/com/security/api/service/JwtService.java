package com.security.api.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // ✅ ИСПРАВЛЕНИЕ: Правильная генерация Key из Base64 secret
    private Key key() {
        try {
            // ✅ Использовать Decoders.BASE64 из io.jsonwebtoken
            // Это поддерживает URL-safe base64 (с символами - и _)
            byte[] decodedKey = Decoders.BASE64.decode(secret);
            
            // ✅ Убедиться, что secret достаточно длинный для HS256
            if (decodedKey.length < 32) {
                logger.warn("JWT secret is shorter than recommended (32 bytes). Current: {} bytes", decodedKey.length);
            }
            
            return Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to decode JWT secret. Make sure it's valid Base64: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT secret configuration", e);
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = allClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims allClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.error("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public String generateToken(UserDetails userDetails) {
        try {
            return Jwts.builder()
                    .setSubject(userDetails.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(key(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            logger.error("Failed to generate JWT token: {}", e.getMessage());
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (JwtException e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}