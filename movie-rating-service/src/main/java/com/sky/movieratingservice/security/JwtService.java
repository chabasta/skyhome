package com.sky.movieratingservice.security;

import com.sky.movieratingservice.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String issueToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(properties.issuer())
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", List.of("USER"))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(properties.ttl())))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtPrincipal parseAndValidate(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .requireIssuer(properties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        UUID userId = UUID.fromString(claims.getSubject());
        String email = claims.get("email", String.class);
        Object rolesRaw = claims.get("roles");
        List<String> roles = rolesRaw instanceof List<?> list
                ? list.stream().map(String::valueOf).toList()
                : List.of();
        return new JwtPrincipal(userId, email, roles);
    }
}
