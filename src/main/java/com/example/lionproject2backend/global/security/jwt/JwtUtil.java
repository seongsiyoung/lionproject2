package com.example.lionproject2backend.global.security.jwt;

import com.example.lionproject2backend.user.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtProperties props;

    public JwtUtil(JwtProperties props) {
        this.props = props;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(props.getSecret())
        );
    }

    public String createAccessToken(Long userId, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .claim("type", TokenType.ACCESS.name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + props.getAccessExpMs()))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public record RefreshTokenResponse(String token, String jti) {}

    public RefreshTokenResponse createRefreshToken(Long userId, String role) {
        long now = System.currentTimeMillis();
        String jti = UUID.randomUUID().toString();
        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)
                .claim("type", TokenType.REFRESH.name())
                .claim("jti", jti)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + props.getRefreshExpMs()))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
        return new RefreshTokenResponse(token, jti);
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validate(String token) {
        parseClaims(token);
    }

    public void validateType(String token, TokenType expected) {
        Claims claims = parseClaims(token);
        String type = claims.get("type", String.class);
        if (type == null || !type.equals(expected.name())) {
            throw new JwtException("Invalid token type");
        }
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public String getJti(String token) {
        return parseClaims(token).get("jti", String.class);
    }

    public String getRole(String token) {
        return parseClaims(token).get("role", String.class);
    }
    public Authentication getAuthentication(String token) {
        Long userId = getUserId(token);
        String role = "ROLE_" + getRole(token);

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        return new UsernamePasswordAuthenticationToken(userId, null, authorities);
    }
}
