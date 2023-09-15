package org.finalproject.tmeroom.auth.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.auth.service.TokenAuthenticationService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
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

    public String createToken(String subject, TokenType tokenType) {
        Claims claims = Jwts.claims().setSubject(subject);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenType.getValidTimeMilli()))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Cookie findCookieByName(String name, HttpServletRequest request) {
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .orElse(new Cookie(name, null));
    }

    public String resolveToken(HttpServletRequest request, TokenType tokenType) {
        Cookie foundCookie = findCookieByName(tokenType.getName(), request);
        return foundCookie.getValue();
    }

    public boolean isTokenValid(String token) {
        try {
            Jws<Claims> claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        MemberDto memberDto = tokenAuthenticationService.getMemberDto(getSubject(token));
        return new UsernamePasswordAuthenticationToken(memberDto, null, memberDto.getAuthorities());
    }

}
