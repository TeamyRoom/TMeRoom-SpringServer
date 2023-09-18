package org.finalproject.TMeRoom.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.TMeRoom.config.TestSecurityConfig;
import org.finalproject.tmeroom.auth.data.dto.request.LoginRequestDto;
import org.finalproject.tmeroom.auth.data.dto.response.LoginResponseDto;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.controller.MemberController;
import org.finalproject.tmeroom.member.data.dto.request.MemberCreateRequestDto;
import org.finalproject.tmeroom.member.data.dto.response.MemberCreateResponseDto;
import org.finalproject.tmeroom.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@DisplayName("컨트롤러 - 배너 서비스")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MemberService memberService;

    @Nested
    @DisplayName("회원가입 요청 테스트")
    class aboutMemberCreate {

        @Test
        @DisplayName("회원가입 요청을 보내면, 정상적인 요청일 때, 성공 코드를 반환한다.")
        void givenProperRequest_whenRequestingLogin_thenReturnsSuccessCodeWithTokenCookie() throws Exception {

            // Given
            String id = "testerGuest";
            String pw = "test";
            String email = "testerGuest@test.com";
            String nickname = "게스트";
            MemberCreateRequestDto requestDto = MemberCreateRequestDto
                    .builder()
                    .memberId(id)
                    .password(pw)
                    .email(email)
                    .nickname(nickname)
                    .build();
            MemberCreateResponseDto responseDto = mock(MemberCreateResponseDto.class);
            given(memberService.createMember(requestDto)).willReturn(responseDto);

            // When & Then
            mockMvc.perform(post("/api/v1/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
        }

    }

}