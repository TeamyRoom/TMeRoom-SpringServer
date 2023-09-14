package org.finalproject.TMeRoom.auth.config.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.finalproject.tmeroom.auth.config.jwt.JwtAuthenticationFilter;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.service.TokenAuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.TestPropertySource;

import java.security.Key;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(classes = {JwtTokenProvider.class})
@TestPropertySource(properties = {
        "JWT_KEY=LongLongLongLongLongLongLongLongTestJWTKey"
})
@DisplayName("JWT 관련 테스트")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @Nested
    @DisplayName("액세스 토큰 발급 기능 테스트")
    class aboutCreatingAccessToken {

        @Test
        @DisplayName("액세스 토큰 발급시, 서브젝트를 넘기면, 액세스 토큰을 발급한다.")
        void givenSubject_whenCreatingAccessToken_thenReturnsAccessToken() {
            // given
            String subject = "tester";

            // when
            String accessToken = jwtTokenProvider.createAccessToken(subject);

            // then
            assertThat(accessToken).isNotNull();
        }
    }
}