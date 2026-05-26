package com.eventix.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // This automatically generates a military-grade HS512 encryption key
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // This sets the ID card to expire in exactly 24 hours (86,400,000 milliseconds)
    private final long jwtExpirationInMs = 86400000;

    // 1. GENERATE THE ID CARD
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // 2. READ THE ID CARD
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 3. VERIFY THE ID CARD IS NOT FAKE
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            System.out.println("Invalid or expired JWT token");
            return false;
        }
    }
}