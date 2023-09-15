package org.finalproject.tmeroom.auth.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.config.jwt.TokenType;
import org.finalproject.tmeroom.auth.data.dto.request.LoginRequestDto;
import org.finalproject.tmeroom.auth.data.dto.response.LoginResponseDto;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    //TODO: 테스트용, 삭제 필요
    @PostConstruct
    public void createTester() {
        memberRepository.save(
                Member.builder()
                        .id("tester00")
                        .pw(passwordEncoder.encode("test"))
                        .nickname("tester")
                        .email("tester@test.com")
                        .role(MemberRole.USER)
                        .build()
        );
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {

        Member member = memberRepository.findById(requestDto.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.getPw(), member.getPw())) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createToken(member.getId(), TokenType.ACCESS);
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .build();
    }


}
