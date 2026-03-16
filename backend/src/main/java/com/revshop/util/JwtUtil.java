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

    // Reset token expires in 15 minutes
    private static final long RESET_TOKEN_EXPIRATION = 15 * 60 * 1000;

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

    // Generate a short-lived JWT token for password reset (15 min expiry)
    public static String generateResetToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("purpose", "PASSWORD_RESET")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + RESET_TOKEN_EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate a reset token — checks signature, expiry, and purpose claim
    public static boolean validateResetToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String purpose = claims.get("purpose", String.class);
            return "PASSWORD_RESET".equals(purpose);

        } catch (Exception e) {
            return false;
        }
    }

    // Extract email from a reset token
    public static String extractEmailFromResetToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
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