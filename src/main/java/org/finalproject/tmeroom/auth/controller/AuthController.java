package org.finalproject.tmeroom.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.auth.config.jwt.TokenType;
import org.finalproject.tmeroom.auth.data.dto.request.LoginRequestDto;
import org.finalproject.tmeroom.auth.data.dto.response.LoginResponseDto;
import org.finalproject.tmeroom.auth.service.AuthService;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Response<Void> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response) {

        LoginResponseDto responseDto = authService.login(requestDto);

        addTokenCookie(response, responseDto.getAccessToken(), TokenType.ACCESS);

        return Response.success();
    }

    private void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType) {
        ResponseCookie tokenCookie = tokenType.createHttpOnlyCookieFrom(token);
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
    }
}
