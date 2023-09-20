package org.finalproject.tmeroom.auth.config;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.auth.config.jwt.JwtAuthenticationFilter;
import org.finalproject.tmeroom.auth.config.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-13
 * 보안 관련 필터 설정
 * 요청이 컨트롤러에 닿기 전에 인증, 인가 및 여러 보안 문제를 사전에 차단하는 기능을 함
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)

                // REST API는 CSRF 대응 필요 없음
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 기본 설정 적용
                .cors(Customizer.withDefaults())

                // 세션 관리 비활성화
                .sessionManagement(
                        (sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URI 인증, 인가 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("api/v1/lecture/**").authenticated()
                        .anyRequest().permitAll())

                // JWT 인증필터 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
