package org.finalproject.tmeroom.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.config.jwt.TokenType;
import org.finalproject.tmeroom.auth.data.dto.request.LoginRequestDto;
import org.finalproject.tmeroom.auth.data.dto.request.RefreshRequestDto;
import org.finalproject.tmeroom.auth.data.dto.response.LoginResponseDto;
import org.finalproject.tmeroom.auth.data.dto.response.RefreshResponseDto;
import org.finalproject.tmeroom.auth.repository.RefreshTokenRepository;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-13
 * 인증 관련 서비스 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {

        Member member = memberRepository.findById(requestDto.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPw(), member.getPw())) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        String memberId = member.getId();

        String accessToken = jwtTokenProvider.createToken(memberId, TokenType.ACCESS);
        String refreshToken = refreshTokenRepository.findRefreshTokenByMemberId(memberId)
                .orElse(jwtTokenProvider.createToken(memberId, TokenType.REFRESH));

        refreshTokenRepository.save(memberId, refreshToken);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public RefreshResponseDto refreshAccessToken(RefreshRequestDto requestDto) {

        String refreshToken = requestDto.getRefreshToken();
        if (refreshToken.isBlank()) {
            throw new ApplicationException(ErrorCode.TOKEN_NOT_FOUND);
        }

        String memberId = jwtTokenProvider.getSubject(refreshToken);
        String foundRequestToken = refreshTokenRepository.findRefreshTokenByMemberId(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.TOKEN_INVALID));

        if (!refreshToken.equals(foundRequestToken)) {
            throw new ApplicationException(ErrorCode.TOKEN_INVALID);
        }

        String accessToken = jwtTokenProvider.createToken(memberId, TokenType.ACCESS);

        refreshTokenRepository.save(memberId, foundRequestToken);

        return RefreshResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public void disableRefreshToken(String refreshToken) {
        if (refreshToken.isBlank()) {
            throw new ApplicationException(ErrorCode.TOKEN_NOT_FOUND);
        }
        String memberId = jwtTokenProvider.getSubject(refreshToken);
        refreshTokenRepository.deleteByMemberId(memberId);
    }
}
