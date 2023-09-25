package org.finalproject.TMeRoom.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.finalproject.TMeRoom.common.config.TestSecurityConfig;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.controller.MemberController;
import org.finalproject.tmeroom.member.data.dto.request.*;
import org.finalproject.tmeroom.member.data.dto.response.MemberCreateResponseDto;
import org.finalproject.tmeroom.member.data.dto.response.ReadMemberResponseDto;
import org.finalproject.tmeroom.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
@WithMockUser
@DisplayName("회원 관련 컨트롤러 테스트")
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

        private MemberCreateRequestDto getMemberCreateRequestDto() {
            String id = "testerGuest";
            String pw = "test";
            String email = "testerGuest@test.com";
            String nickname = "게스트";
            MemberCreateRequestDto requestDto = new MemberCreateRequestDto();
            requestDto.setMemberId(id);
            requestDto.setPassword(pw);
            requestDto.setEmail(email);
            requestDto.setNickname(nickname);
            return requestDto;
        }

        @Test
        @DisplayName("회원가입 요청을 보내면, 정상적인 요청일 때, 성공 코드를 반환한다.")
        void givenProperRequest_whenRequestingLogin_thenReturnsSuccessCodeWithTokenCookie() throws Exception {

            // Given
            MemberCreateRequestDto requestDto = getMemberCreateRequestDto();
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

        @Test
        @DisplayName("회원가입 요청을 보내면, 아이디가 중복일 때, 에러 코드를 반환한다.")
        void givenDuplicateId_whenRequestingLogin_thenReturnsErrorCode() throws Exception {

            // Given
            MemberCreateRequestDto requestDto = getMemberCreateRequestDto();
            MemberCreateResponseDto responseDto = mock(MemberCreateResponseDto.class);
            given(memberService.createMember(requestDto)).willThrow(new ApplicationException(ErrorCode.DUPLICATE_ID));

            // When & Then
            mockMvc.perform(post("/api/v1/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
        }

        @Test
        @DisplayName("회원가입 요청을 보내면, 이메일이 중복일 때, 에러 코드를 반환한다.")
        void givenDuplicateEmail_whenRequestingLogin_thenReturnsErrorCode() throws Exception {

            // Given
            MemberCreateRequestDto requestDto = getMemberCreateRequestDto();
            MemberCreateResponseDto responseDto = mock(MemberCreateResponseDto.class);
            given(memberService.createMember(requestDto)).willThrow(
                    new ApplicationException(ErrorCode.DUPLICATE_EMAIL));

            // When & Then
            mockMvc.perform(post("/api/v1/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
        }
    }

    @Nested
    @DisplayName("아이디 중복 여부 확인 테스트")
    class aboutCheckingIdDuplicate {

        @Test
        @DisplayName("아이디 중복 체크 할 때, 중복된 아이디라면, 성공 코드와 함께 참을 반환한다.")
        void givenDuplicateId_whenCheckingIdDuplicate_thenReturnsTrue() throws Exception {

            // Given
            String memberId = "testerUser";
            given(memberService.isIdDuplicate(memberId)).willReturn(true);

            // When & Then
            mockMvc.perform(get("/api/v1/member/id/duplicate/" + memberId)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists())
                    .andExpect(jsonPath("$..result").value(true));
        }
    }

    @Nested
    @DisplayName("이메일 중복 여부 확인 테스트")
    class aboutCheckingEmailDuplicate {

        @Test
        @DisplayName("이메일 중복 체크 할 때, 중복된 이메일이라면, 성공 코드와 함께 참을 반환한다.")
        void givenDuplicateEmail_whenCheckingIdDuplicate_thenReturnsTrue() throws Exception {

            // Given
            String memberEmail = "tester@test.com";
            given(memberService.isEmailDuplicate(memberEmail)).willReturn(true);

            // When & Then
            mockMvc.perform(get("/api/v1/member/email/duplicate/" + memberEmail)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists())
                    .andExpect(jsonPath("$..result").value(true));
        }
    }

    @Nested
    @DisplayName("인증 메일 재전송 테스트")
    class aboutResendingConfirmMail {

        @Test
        @DisplayName("인증 메일 재전송 요청 할 때, 정상적인 요청이라면, 성공 코드를 반환한다.")
        void givenProperRequest_whenResendingConfirmMail_thenReturnsSuccessCode() throws Exception {

            // Given

            // When & Then
            mockMvc.perform(get("/api/v1/member/email/confirm/resend")
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
        }
    }

    @Nested
    @DisplayName("메일 인증 테스트")
    class aboutConfirmingEmail {

        @Test
        @DisplayName("메일 인증을 요청 할 때, 정상적인 요청이라면, 성공 코드를 반환한다.")
        void givenProperRequest_whenConfirmingEmail_thenReturnsSuccessCode() throws Exception {

            // Given
            String confirmCode = "confirmCode";
            doNothing().when(memberService).confirmEmail(confirmCode);

            // When & Then
            mockMvc.perform(put("/api/v1/member/email/confirm/" + confirmCode)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
        }

        @Test
        @DisplayName("메일 인증을 요청 할 때, 잘못된 코드라면, 반환한다.")
        void givenWrongCode_whenConfirmingEmail_thenReturnsErrorCode() throws Exception {

            // Given
            String confirmCode = "wrongConfirmCode";
            doThrow(new ApplicationException(ErrorCode.CODE_NOT_VALID)).when(memberService).confirmEmail(confirmCode);

            // When & Then
            mockMvc.perform(put("/api/v1/member/email/confirm/" + confirmCode)
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
        }
    }

    @Nested
    @DisplayName("프로필 조회 테스트")
    class aboutGettingProfile {

        private ReadMemberResponseDto getReadMemberResponseDto() {
            ReadMemberResponseDto responseDto = new ReadMemberResponseDto();
            responseDto.setEmail("tester@test.test.com");
            responseDto.setMemberId("testUser");
            responseDto.setNickname("tester");
            return responseDto;
        }

        @Test
        @DisplayName("정상적인 요청이라면, 프로필 조회시, 프로필 정보를 반환한다.")
        void givenProperRequest_whenReadingProfile_thenReturnsProfileInfo() throws Exception {

            // Given
            ReadMemberResponseDto responseDto = getReadMemberResponseDto();
            given(memberService.readMember(any())).willReturn(responseDto);

            // When & Then
            mockMvc.perform(get("/api/v1/member")
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
            ;
        }
    }

    @Nested
    @DisplayName("프로필 수정 테스트")
    class aboutUpdatingProfile {

        private MemberUpdateRequestDto getRequestDto() {
            MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto();
            requestDto.setNickname("newNickname");
            return requestDto;
        }

        @Test
        @DisplayName("정상적인 요청이라면, 프로필 수정시, 성공 코드를 반환한다.")
        void givenProperRequest_whenUpdatingProfile_thenReturnsSuccessCode() throws Exception {

            // Given
            MemberUpdateRequestDto requestDto = getRequestDto();
            doNothing().when(memberService).updateMember(any(), any());

            // When & Then
            mockMvc.perform(put("/api/v1/member")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
            ;
        }
    }

    @Nested
    @DisplayName("비밀번호 수정 테스트")
    class aboutUpdatingPassword {

        private PasswordUpdateRequestDto getRequestDto() {
            PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto();
            requestDto.setOldPassword("oldPassword");
            requestDto.setNewPassword("newPassword");
            return requestDto;
        }

        @Test
        @DisplayName("정상적인 요청이라면, 비밀번호 수정시, 성공 코드를 반환한다.")
        void givenProperRequest_whenUpdatingProfile_thenReturnsSuccessCode() throws Exception {

            // Given
            PasswordUpdateRequestDto requestDto = getRequestDto();
            doNothing().when(memberService).updatePassword(any(), any());

            // When & Then
            mockMvc.perform(put("/api/v1/member/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
            ;
        }

        @Test
        @DisplayName("잘못된 비밀번호라면, 비밀번호 수정시, 실패 코드를 반환한다.")
        void givenWrongPassword_whenUpdatingProfile_thenReturnsErrorCode() throws Exception {

            // Given
            PasswordUpdateRequestDto requestDto = getRequestDto();
            doThrow(new ApplicationException(ErrorCode.INVALID_PASSWORD)).when(memberService)
                    .updatePassword(any(), any());

            // When & Then
            mockMvc.perform(put("/api/v1/member/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
            ;
        }
    }

    @Nested
    @DisplayName("회원 삭제 테스트")
    class aboutDeletingMember {

        @Test
        @DisplayName("정상적인 요청이라면, 회원 탈퇴시, 성공 코드를 반환한다.")
        void givenProperRequest_whenDeletingProfile_thenReturnsSuccessCode() throws Exception {

            // Given
            doNothing().when(memberService).deleteMember(any());

            // When & Then
            mockMvc.perform(delete("/api/v1/member")
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
            ;
        }
    }

    @Nested
    @DisplayName("잊어버린 아이디 조회 테스트")
    class aboutSendingLostId {

        @Test
        @DisplayName("정상적인 요청이라면, 아이디 조회 메일 전송시, 성공 코드를 반환한다.")
        void givenProperRequest_whenSendingLostId_thenReturnsSuccessCode() throws Exception {

            // Given
            String email = "tester@test.com";
            doNothing().when(memberService).sendId(any());

            // When & Then
            mockMvc.perform(get("/api/v1/member/id/lost?email=" + email)
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
        }

        @Test
        @DisplayName("존재하지 않는 이메일이라면, 아이디 조회 메일 전송시, 실패 코드를 반환한다.")
        void givenInvalidEmail_whenSendingLostId_thenReturnsErrorCode() throws Exception {

            // Given
            String email = "tester@test.com";
            doThrow(new ApplicationException(ErrorCode.USER_NOT_FOUND)).when(memberService).sendId(any());

            // When & Then
            mockMvc.perform(get("/api/v1/member/id/lost?email=" + email)
                    ).andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정 링크 전송 테스트")
    class aboutSendingPasswordResetCode {

        private MemberSendResetCodeRequestDto getRequestDto() {
            MemberSendResetCodeRequestDto requestDto = new MemberSendResetCodeRequestDto();
            requestDto.setEmail("tester@test.com");
            requestDto.setMemberId("tester00");
            return requestDto;
        }

        @Test
        @DisplayName("정상적인 요청이라면, 비밀번호 재설정 메일 전송시, 성공 코드를 반환한다.")
        void givenProperRequest_whenSendingPasswordResetCode_thenReturnsSuccessCode() throws Exception {

            // Given
            MemberSendResetCodeRequestDto requestDto = getRequestDto();
            doNothing().when(memberService).sendPasswordResetCode(any());

            // When & Then
            mockMvc.perform(post("/api/v1/member/password/lost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
            ;
        }

        @Test
        @DisplayName("존재하지 않는 이메일이라면, 비밀번호 재설정 메일 전송시, 실패 코드를 반환한다.")
        void givenInvalidEmail_whenSendingPasswordResetCode_thenReturnsErrorCode() throws Exception {

            // Given
            MemberSendResetCodeRequestDto requestDto = getRequestDto();
            doThrow(new ApplicationException(ErrorCode.USER_NOT_FOUND)).when(memberService)
                    .sendPasswordResetCode(any());

            // When & Then
            mockMvc.perform(post("/api/v1/member/password/lost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
            ;
        }

        @Test
        @DisplayName("아이디가 다르다면, 비밀번호 재설정 메일 전송시, 실패 코드를 반환한다.")
        void givenWrongMemberId_whenSendingPasswordResetCode_thenReturnsErrorCode() throws Exception {

            // Given
            MemberSendResetCodeRequestDto requestDto = getRequestDto();
            doThrow(new ApplicationException(ErrorCode.INVALID_ID)).when(memberService).sendPasswordResetCode(any());

            // When & Then
            mockMvc.perform(post("/api/v1/member/password/lost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
            ;
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정 테스트")
    class aboutResettingPassword {

        private PasswordResetRequestDto getPasswordResetRequestDto() {
            PasswordResetRequestDto requestDto = new PasswordResetRequestDto();
            requestDto.setResetCode("resetCode");
            requestDto.setNewPassword("newPassword");
            return requestDto;
        }

        @Test
        @DisplayName("정상적인 요청이라면, 비밀번호 재설정시, 성공 코드를 반환한다.")
        void givenProperRequest_whenResettingPassword_thenReturnsSuccessCode() throws Exception {

            // Given
            PasswordResetRequestDto requestDto = getPasswordResetRequestDto();
            doNothing().when(memberService).resetPassword(requestDto);

            // When & Then
            mockMvc.perform(put("/api/v1/member/password/lost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode == 'SUCCESS')]").exists());
        }

        @Test
        @DisplayName("정상적인 요청이라면, 비밀번호 재설정시, 성공 코드를 반환한다.")
        void givenInvalidResetCode_whenResettingPassword_thenReturnsErrorCode() throws Exception {

            // Given
            PasswordResetRequestDto requestDto = getPasswordResetRequestDto();
            doThrow(new ApplicationException(ErrorCode.CODE_NOT_VALID)).when(memberService).resetPassword(requestDto);

            // When & Then
            mockMvc.perform(put("/api/v1/member/password/lost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(requestDto))
                    ).andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[?(@.resultCode != 'SUCCESS')]").exists());
        }
    }
}