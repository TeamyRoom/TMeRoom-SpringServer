package org.finalproject.TMeRoom.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.tmeroom.auth.config.SecurityConfig;
import org.finalproject.tmeroom.auth.controller.AuthController;
import org.finalproject.tmeroom.auth.data.dto.request.LoginRequestDto;
import org.finalproject.tmeroom.auth.data.dto.response.LoginResponseDto;
import org.finalproject.tmeroom.auth.service.AuthService;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthService authService;

    @Nested
    @DisplayName("로그인 요청 테스트")
    class aboutLogin {

        @Test
        @DisplayName("로그인 요청을 보내면, 정상적인 요청일 때, 성공 코드와 토큰을 담은 쿠키를 응답으로 반환한다.")
        void givenProperRequest_whenRequestingLogin_thenReturnsSuccessCodeWithTokenCookie() throws Exception {

            // Given
            String id = "tester00";
            String password = "test";
            LoginRequestDto requestDto = new LoginRequestDto();
            requestDto.setId(id);
            requestDto.setPw(password);
            LoginResponseDto responseDto = LoginResponseDto.builder()
                    .accessToken("mockAccessToken")
                    .build();
            given(authService.login(any(LoginRequestDto.class))).willReturn(responseDto);

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.[?(@.resultCode == 'SUCCESS')]").exists())
                    .andExpect(result -> {
                        List<String> setCookieHeaders = result.getResponse().getHeaders(HttpHeaders.SET_COOKIE);
                        assertThat(setCookieHeaders).isNotNull().hasSize(1);
                        String setCookieHeaderValue = setCookieHeaders.get(0);
                        assertThat(setCookieHeaderValue).contains("accessToken");
                        assertThat(setCookieHeaderValue).contains("HttpOnly");
                        assertThat(setCookieHeaderValue).contains("Max-Age");
                    });
        }

        @Test
        @DisplayName("로그인 요청을 보내면, 잘못된 아이디라면, 에러 코드를 반환한다.")
        void givenWrongId_whenRequestingLogin_thenReturnsIsNotFound() throws Exception {
            // Given
            String id = "wrongId";
            String password = "password";
            LoginRequestDto requestDto = new LoginRequestDto();
            requestDto.setId(id);
            requestDto.setPw(password);
            given(authService.login(any(LoginRequestDto.class))).willThrow(
                    new ApplicationException(ErrorCode.USER_NOT_FOUND)
            );

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.[?(@.resultCode == 'USER_NOT_FOUND')]").exists());
        }

        @Test
        @DisplayName("로그인 요청을 보내면, 잘못된 비밀번호라면, 에러 코드를 반환한다.")
        void givenWrongPassword_whenRequestingLogin_thenReturnsIsUnauthorized() throws Exception {
            // Given
            String id = "tester";
            String password = "wrongPw";
            LoginRequestDto requestDto = new LoginRequestDto();
            requestDto.setId(id);
            requestDto.setPw(password);
            given(authService.login(any(LoginRequestDto.class))).willThrow(
                    new ApplicationException(ErrorCode.INVALID_PASSWORD)
            );

            // When & Then
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.[?(@.resultCode == 'INVALID_PASSWORD')]").exists());
        }
    }

}