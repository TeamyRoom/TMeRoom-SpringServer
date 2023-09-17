package org.finalproject.TMeRoom.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.config.jwt.TokenType;
import org.finalproject.tmeroom.auth.data.dto.request.LoginRequestDto;
import org.finalproject.tmeroom.auth.data.dto.response.LoginResponseDto;
import org.finalproject.tmeroom.auth.service.AuthService;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = {AuthService.class})
@TestPropertySource(properties = {"JWT_KEY=LongLongLongLongLongLongLongLongTestJWTKey"})
@DisplayName("인증 서비스 로직 테스트")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("로그인 기능 테스트")
    class aboutLogin {

        @Test
        @DisplayName("로그인 요청시, 올바른 요청이라면, 토큰을 발급한다.")
        void givenProperRequestDto_whenLoggingIn_thenReturnsToken() {
            // Given
            LoginRequestDto mockRequestDto = getMockRequestDto();
            Member mockMember = getMockMember();
            String mockAccessToken = "mockAccessToken";
            given(memberRepository.findById(mockRequestDto.getId())).willReturn(Optional.of(mockMember));
            given(passwordEncoder.matches(mockRequestDto.getPw(), mockMember.getPw())).willReturn(true);
            given(jwtTokenProvider.createToken(mockMember.getId(), TokenType.ACCESS)).willReturn(mockAccessToken);

            // When
            LoginResponseDto responseDto = authService.login(mockRequestDto);

            // Then
            then(memberRepository).should().findById(mockRequestDto.getId());
            then(passwordEncoder).should().matches(mockRequestDto.getPw(), mockMember.getPw());
            then(jwtTokenProvider).should().createToken(mockMember.getId(), TokenType.ACCESS);
            assertThat(responseDto)
                    .hasFieldOrPropertyWithValue("accessToken", mockAccessToken);
        }

        @Test
        @DisplayName("로그인 요청시, 존재하지 않는 유저ID라면, 예외를 발생시킨다.")
        void givenWrongUserId_whenLoggingIn_thenThrowsUserNotFoundException() {
            // Given
            LoginRequestDto mockRequestDto = getMockRequestDto();
            given(memberRepository.findById(mockRequestDto.getId())).willReturn(Optional.empty());

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> authService.login(mockRequestDto));

            // Then
            then(memberRepository).should().findById(mockRequestDto.getId());
            then(passwordEncoder).should().encode(any());
            then(passwordEncoder).shouldHaveNoMoreInteractions();
            then(jwtTokenProvider).shouldHaveNoInteractions();
            assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("로그인 요청시, 잘못된 비밀번호라면, 예외를 발생시킨다.")
        void givenWrongPassword_whenLoggingIn_thenThrowsUserNotFoundException() {
            // Given
            LoginRequestDto mockRequestDto = getMockRequestDto();
            Member mockMember = getMockMember();
            given(memberRepository.findById(mockRequestDto.getId())).willReturn(Optional.of(mockMember));
            given(passwordEncoder.matches(mockRequestDto.getPw(), mockMember.getPw())).willReturn(false);

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> authService.login(mockRequestDto));

            // Then
            then(memberRepository).should().findById(mockRequestDto.getId());
            then(passwordEncoder).should().matches(mockRequestDto.getPw(), mockMember.getPw());
            then(jwtTokenProvider).shouldHaveNoInteractions();
            assertEquals(e.getErrorCode(), ErrorCode.INVALID_PASSWORD);
        }
    }

    private LoginRequestDto getMockRequestDto() {
        return LoginRequestDto.builder()
                .id("tester00")
                .pw("test")
                .build();
    }

    private Member getMockMember() {
        return Member.builder()
                .id("tester00")
                .pw("encodedPw")
                .email("tester@test.com")
                .nickname("tester")
                .role(MemberRole.USER)
                .build();
    }
}