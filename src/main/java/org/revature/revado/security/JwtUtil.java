package org.revature.revado.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Tells Spring to create this class as a bean so it can be injected where needed
public class JwtUtil {

    // Secret signing key used to generate and validate JWT tokens
    private final Key key;

    // Token expiration time (read from application.properties)
    private final long expirationMs;

    // Constructor: reads jwt.secret and jwt.expiration-ms from application.properties
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration-ms}") long expirationMs) {

        // Convert secret string into signing key meaning You are converting that plain string into a cryptographic key object.
        this.key = Keys.hmacShaKeyFor(secret.getBytes());

        // Save expiration time
        this.expirationMs = expirationMs;
    }

    // Generate JWT token after successful login
    public String generateToken(String username) {

        Date now = new Date(); // current time
        Date expiry = new Date(now.getTime() + expirationMs); // calculate expiry time

        return Jwts.builder()
                .setSubject(username) // PAYLOAD: store username inside token
                .setIssuedAt(now) // PAYLOAD: token creation time
                .setExpiration(expiry) // PAYLOAD: token expiration time
                .signWith(key, SignatureAlgorithm.HS256) // sign token with secret key // HEADER + SIGNATURE HS256 Secure Hash Algorithm 256-bit
                .compact(); // Finalizes and returns token as a string.
    }

    // Extract username from token payload
    public String extractUsername(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    // Check if token is valid (not expired)
    public boolean isValid(String token) {
        try {
            parseClaims(token); // try to parse token
            return true; // token is valid
        } catch (JwtException | IllegalArgumentException e) {
            return false; // token is invalid or expired
        }
    }

    // parseClaims validates and reads the token during every request to ensure it hasn’t been tampered with or expired.
    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // verify using secret key
                .build()
                .parseClaimsJws(token); // parse and validate token WT library automatically checks:Signature matches secret key and Expiry time is valid and token structure is correct
    }
}
