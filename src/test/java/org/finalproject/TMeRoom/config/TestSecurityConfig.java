package org.finalproject.TMeRoom.config;

import org.finalproject.tmeroom.auth.config.SecurityConfig;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.service.TokenAuthenticationService;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.BDDMockito.given;

@Import({SecurityConfig.class, JwtTokenProvider.class, TokenAuthenticationService.class})
@TestPropertySource(properties = {
        "JWT_KEY=LongLongLongLongLongLongLongLongTestJWTKey"
})
@TestConfiguration
public class TestSecurityConfig {

    @MockBean
    private MemberRepository memberRepository;

    @BeforeTestMethod
    public void securitySetUp() {
        Member mockAdminAccount = Member.builder()
                .email("testAdmin@test.com")
                .id("testAdmin")
                .pw("password")
                .role(MemberRole.ADMIN)
                .build();
        given(memberRepository.findById("testAdmin")).willReturn(Optional.of(mockAdminAccount));

        Member mockUserAccount = Member.builder()
                .email("testUser@test.com")
                .id("testUser")
                .pw("password")
                .role(MemberRole.USER)
                .build();
        given(memberRepository.findById("testUser")).willReturn(Optional.of(mockUserAccount));

        Member mockGuestAccount = Member.builder()
                .email("testGuest@test.com")
                .id("testGuest")
                .pw("password")
                .role(MemberRole.GUEST)
                .build();
        given(memberRepository.findById("testGuest")).willReturn(Optional.of(mockGuestAccount));
    }
}
