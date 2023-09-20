package org.finalproject.tmeroom.auth.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-20
 * 인증 실패 시 응답 처리
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Map<String, ErrorCode> authErrorMap = Stream.of(ErrorCode.TOKEN_NOT_FOUND,
                    ErrorCode.TOKEN_INVALID)
            .collect(Collectors.toUnmodifiableMap(ErrorCode::name, e -> e));

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ErrorCode errorCode = resolveErrorCode(response.getHeader("jwt-error"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getStatus().value());
        response.getWriter().write(Response.error(errorCode.name(), errorCode.getMessage()).toStream());
    }

    private ErrorCode resolveErrorCode(String errorName) {
        return authErrorMap.getOrDefault(errorName, ErrorCode.AUTHENTICATION_ERROR);
    }
}