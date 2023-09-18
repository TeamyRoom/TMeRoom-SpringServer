package org.finalproject.TMeRoom.auth.config.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.config.jwt.TokenType;
import org.finalproject.tmeroom.auth.service.TokenAuthenticationService;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;


@SpringBootTest(classes = {JwtTokenProvider.class})
@TestPropertySource(properties = {"JWT_KEY=LongLongLongLongLongLongLongLongTestJWTKey"})
@DisplayName("JWT 관련 테스트")
class JwtTokenProviderTest {

    private final String masterToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhY2Nlc3NUb2tlbiIsInN1YiI6InRlc3RlciIsImlhdCI6MTY5NDc2MTE3NSwiZXhwIjo5MjIzMzcyMDM2ODU0Nzc1fQ.SUaqRU7eASLiN1Rw2AM5dJqd2a8hxpgTHLBxzzJ15MI";
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;
    @MockBean
    private HttpServletRequest request;

    private Cookie createCookie(String key, String value) {
        return new Cookie(key, value);
    }

    private MemberDto getMockMemberDto() {
        return MemberDto.builder()
                .id("tester00")
                .email("tester@test.com")
                .nickname("tester")
                .role(MemberRole.GUEST)
                .build();
    }

    @Nested
    @DisplayName("토큰 발급 기능 테스트")
    class aboutCreatingAccessToken {

        @Test
        @DisplayName("액세스 토큰 발급시, 서브젝트를 넘기면, 액세스 토큰을 발급한다.")
        void givenSubject_whenCreatingAccessToken_thenReturnsAccessToken() {
            // given
            String subject = "tester";

            // when
            String accessToken = jwtTokenProvider.createToken(subject, TokenType.ACCESS);

            // then
            assertThat(accessToken).isNotNull();
            System.out.println(accessToken);
        }
    }

    @Nested
    @DisplayName("토큰 추출 기능 테스트")
    class aboutResolvingToken {

        @Test
        @DisplayName("액세스 토큰 추출시, 올바른 요청이라면, 추출된 액세스 토큰을 반환한다.")
        void givenProperRequest_whenResolvingAccessToken_thenReturnsResolvedAccessToken() {
            // given
            String expectedToken = "ACCESS_TOKEN_STRING";
            Cookie accessTokenCookie = createCookie("accessToken", expectedToken);
            Cookie[] cookies = new Cookie[]{accessTokenCookie};
            given(request.getCookies()).willReturn(cookies);

            // when
            String accessToken = jwtTokenProvider.resolveToken(request, TokenType.ACCESS);

            // then
            assertThat(accessToken).isEqualTo(expectedToken);
        }

        @Test
        @DisplayName("액세스 토큰 추출시, 토큰이 없는 요청이라면, null 값을 반환한다.")
        void givenRequestWithoutToken_whenResolvingAccessToken_thenReturnsNull() {
            // given
            Cookie[] cookies = new Cookie[]{};
            given(request.getCookies()).willReturn(cookies);

            // when
            String accessToken = jwtTokenProvider.resolveToken(request, TokenType.ACCESS);

            // then
            assertThat(accessToken).isEqualTo(null);
        }
    }

    @Nested
    @DisplayName("토큰 검증 기능 테스트")
    class aboutValidatingToken {

        @Test
        @DisplayName("액세스 토큰 검증시, 유효한 토큰이라면, 참을 반환한다.")
        void givenProperAccessToken_whenValidatingAccessToken_thenReturnsTrue() {
            // given
            String validToken = masterToken;

            // when
            boolean isTokenValid = jwtTokenProvider.isTokenValid(validToken);

            // then
            assertThat(isTokenValid).isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("권한 조회 기능 테스트")
    class aboutAuthenticatingToken {

        @Test
        @DisplayName("액세스 토큰으로부터 권한 조회시, 유효한 사용자 ID를 가지고 있다면, 사용자 정보가 담긴 DTO를 반환한다.")
        void givenAccessTokenWithProperMemberId_whenGettingAuthentication_thenReturnsMemberDto() {
            // given
            String validToken = masterToken;
            MemberDto mockMemberDto = getMockMemberDto();
            given(tokenAuthenticationService.getMemberDto("tester")).willReturn(mockMemberDto);
            UsernamePasswordAuthenticationToken expectedUpat = new UsernamePasswordAuthenticationToken(mockMemberDto, null, mockMemberDto.getAuthorities());

            // when
            UsernamePasswordAuthenticationToken receivedUpat = jwtTokenProvider.getAuthentication(validToken);

            // then
            assertThat(receivedUpat.getName()).isEqualTo(expectedUpat.getName());
            assertThat(receivedUpat.getCredentials()).isEqualTo(receivedUpat.getCredentials());
        }
    }
}