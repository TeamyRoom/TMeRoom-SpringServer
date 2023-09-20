package org.finalproject.tmeroom.auth.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-18
 * 요청에서 JWT를 추출하고 그에 담긴 회원 정보를 조회해 인증 정보를 부여
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 토큰 추출
            String token = jwtTokenProvider.resolveToken(request, TokenType.ACCESS);

            // 토큰 유효성 체크
            if (token == null) {
                writeResult(response, ErrorCode.TOKEN_NOT_FOUND);
                filterChain.doFilter(request, response);
                return;
            }

            if (!jwtTokenProvider.isTokenValid(token)) {
                writeResult(response, ErrorCode.TOKEN_INVALID);
                filterChain.doFilter(request, response);
                return;
            }
            // Authentication 부여
            UsernamePasswordAuthenticationToken authentication = jwtTokenProvider.getAuthentication(token);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (RuntimeException e) {
            writeResult(response, ErrorCode.AUTHENTICATION_ERROR);
            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void writeResult(HttpServletResponse response, ErrorCode errorCode) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getStatus().value());
        response.getWriter().write(Response.error(errorCode.name(), errorCode.getMessage()).toStream());
    }
}