package org.finalproject.tmeroom.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.config.jwt.TokenType;
import org.finalproject.tmeroom.auth.data.dto.request.LoginRequestDto;
import org.finalproject.tmeroom.auth.data.dto.request.RefreshRequestDto;
import org.finalproject.tmeroom.auth.data.dto.response.LoginResponseDto;
import org.finalproject.tmeroom.auth.data.dto.response.RefreshResponseDto;
import org.finalproject.tmeroom.auth.service.AuthService;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvide;

    @PostMapping("/login")
    public Response<Void> login(@RequestBody @Valid LoginRequestDto requestDto, HttpServletResponse response) {

        LoginResponseDto responseDto = authService.login(requestDto);

        addTokenCookie(response, responseDto.getAccessToken(), TokenType.ACCESS);
        addTokenCookie(response, responseDto.getRefreshToken(), TokenType.REFRESH);

        return Response.success();
    }

    @PostMapping("/refresh")
    public Response<Void> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = parseTokenCookie(request, TokenType.REFRESH);
        RefreshRequestDto requestDto = RefreshRequestDto.builder()
                .refreshToken(refreshToken)
                .build();

        RefreshResponseDto responseDto = authService.refreshAccessToken(requestDto);

        addTokenCookie(response, responseDto.getAccessToken(), TokenType.ACCESS);
        addTokenCookie(response, responseDto.getRefreshToken(), TokenType.REFRESH);

        return Response.success();

    }

    @DeleteMapping("/refresh")
    public Response<Void> disableRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = parseTokenCookie(request, TokenType.REFRESH);
        authService.disableRefreshToken(refreshToken);

        removeTokenCookie(response, TokenType.ACCESS);
        removeTokenCookie(response, TokenType.REFRESH);

        return Response.success();
    }

    @GetMapping()
    public Response<Void> authenticateToken(@RequestParam String accessToken) {
        if (jwtTokenProvide.isTokenValid(accessToken)) {
            return Response.success();
        }
        return Response.error(ErrorCode.TOKEN_INVALID.getMessage());
    }

    private void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType) {
        ResponseCookie tokenCookie = tokenType.createCookieFrom(token);
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
    }

    private String parseTokenCookie(HttpServletRequest request, TokenType tokenType) {
        String tokenName = tokenType.getName();
        return Arrays.stream(request.getCookies())
                .filter(cookie -> tokenName.equals(cookie.getName()))
                .findFirst()
                .orElse(new Cookie(tokenName, ""))
                .getValue();
    }

    private void removeTokenCookie(HttpServletResponse response, TokenType tokenType) {
        ResponseCookie tokenCookie = tokenType.removeCookie();
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
    }
}
