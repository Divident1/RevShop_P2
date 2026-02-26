package com.revshop.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static final SecretKey KEY =
            Keys.hmacShaKeyFor("mysecretkeymysecretkeymysecretkey12".getBytes());

    private static final long EXPIRATION = 86400000; // 1 day


    public static String generateToken(String email) {

        return Jwts.builder()
                .setSubject(email)  // FIXED
                .setIssuedAt(new Date())  // FIXED
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))  // FIXED
                .signWith(KEY, SignatureAlgorithm.HS256)  // FIXED
                .compact();
    }


    public static String extractEmail(String token) {

        Claims claims = Jwts.parserBuilder()   // FIXED
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }


    public static boolean validateToken(String token) {

        try {

            Jwts.parserBuilder()   // FIXED
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token);

            return true;

        } catch (Exception e) {

            return false;

        }

    }

}