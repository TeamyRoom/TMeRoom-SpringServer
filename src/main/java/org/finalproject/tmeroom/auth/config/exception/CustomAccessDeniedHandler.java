package org.finalproject.tmeroom.auth.config.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-20
 * 인가 실패 시 응답 처리
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException exception) throws IOException {
        log.info(exception.getMessage() +
                " for request of URI: [" + request.getRequestURI() + "]" +
                " requested by Account: " + request.getUserPrincipal().getName());
        ErrorCode errorCode = ErrorCode.AUTHORIZATION_ERROR;
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getStatus().value());
        response.getWriter().write(Response.error(errorCode.name(), errorCode.getMessage()).toStream());
    }
}