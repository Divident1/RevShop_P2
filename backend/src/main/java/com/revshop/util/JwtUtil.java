package com.revshop.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey KEY = Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkey12".getBytes());

    private static final long EXPIRATION = 86400000;

    // generate token with role
    public static String generateToken(String email, String role, Long userId) {

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String extractEmail(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static String extractRole(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    public static boolean validateToken(String token) {

        try {

            Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (Exception e) {

            return false;

        }

    }

}