package org.finalproject.tmeroom.auth.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.auth.service.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-13
 * JWT를 발급하고, 검증하는 로직을 담은 클래스
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final TokenAuthenticationService tokenAuthenticationService;

    private final long ACCESS_TOKEN_VALID_MILLISECOND = 1000L * 60 * 60;
    @Value("${jwt.secret.key}")
    private String JWT_SECRET_KEY;
    private byte[] KEY_BYTES;

    @PostConstruct
    private void init() {
        KEY_BYTES = JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(KEY_BYTES);
    }

    public String createAccessToken(String subject) {
        Claims claims = Jwts.claims().setSubject(subject);
        Date now = new Date();
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_MILLISECOND))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }
}
