package org.finalproject.tmeroom.auth.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-20
 * 인증 실패 시 응답 처리
 * 실제 메시지는 JwtAuthenticationFilter에서 담지만, 이 클래스가 없으면 메시지 자체가 담기지 않음
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {}
}