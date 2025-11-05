package com.platformone.user.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parser().setSigningKey(key()).build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public String extractRole(String token) {
        return (String) Jwts.parser().setSigningKey(key()).build()
                .parseClaimsJws(token)
                .getBody().get("role");
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        String role = extractRole(token);
        if (role == null || role.isEmpty()) {
            return Collections.emptyList();
        }

        String authorityString = role.toUpperCase().startsWith("ROLE_") ? role.toUpperCase() : "ROLE_" + role.toUpperCase();
        GrantedAuthority authority = new SimpleGrantedAuthority(authorityString);
        return Collections.singletonList(authority);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}