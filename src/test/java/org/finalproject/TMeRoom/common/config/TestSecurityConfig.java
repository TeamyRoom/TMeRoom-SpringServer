package org.finalproject.TMeRoom.common.config;

import org.finalproject.tmeroom.auth.config.SecurityConfig;
import org.finalproject.tmeroom.auth.config.exception.CustomAccessDeniedHandler;
import org.finalproject.tmeroom.auth.config.exception.CustomAuthenticationEntryPoint;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.finalproject.tmeroom.auth.service.TokenAuthenticationService;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.FilterChainProxy;

@Import({SecurityConfig.class, JwtTokenProvider.class, TokenAuthenticationService.class,
        CustomAccessDeniedHandler.class, CustomAuthenticationEntryPoint.class})
public class TestSecurityConfig {

    @MockBean
    private MemberRepository memberRepository;
}
