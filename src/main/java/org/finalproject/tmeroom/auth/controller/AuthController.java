package org.finalproject.tmeroom.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.config.jwt.TokenType;
import org.finalproject.tmeroom.auth.data.dto.request.LoginRequestDto;
import org.finalproject.tmeroom.auth.data.dto.response.LoginResponseDto;
import org.finalproject.tmeroom.auth.service.AuthService;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

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

        return Response.success();
    }

    @GetMapping("/sfu/{lectureCode}/{accessToken}")
    public Response<Void> sfu(@PathVariable String lectureCode, @PathVariable String accessToken) {
        if(jwtTokenProvide.isTokenValid(accessToken)) return Response.success();
        return Response.error(ErrorCode.TOKEN_INVALID.getMessage());
    }

    private void addTokenCookie(HttpServletResponse response, String token, TokenType tokenType) {
        ResponseCookie tokenCookie = tokenType.createHttpOnlyCookieFrom(token);
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
    }
}
