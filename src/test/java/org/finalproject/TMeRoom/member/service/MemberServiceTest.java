package org.finalproject.TMeRoom.member.service;

import org.finalproject.TMeRoom.common.util.MockProvider;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.common.service.MailService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.dto.request.*;
import org.finalproject.tmeroom.member.data.dto.response.MemberCreateResponseDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.EmailConfirmCodeRepository;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.finalproject.tmeroom.member.repository.PasswordResetCodeRepository;
import org.finalproject.tmeroom.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.finalproject.TMeRoom.common.util.MockProvider.getMockGuestMember;
import static org.finalproject.TMeRoom.common.util.MockProvider.getMockUserMember;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = {MemberService.class})
@Import(value = MockProvider.class)
@DisplayName("인증 서비스 로직 테스트")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private EmailConfirmCodeRepository emailConfirmCodeRepository;
    @MockBean
    private PasswordResetCodeRepository passwordResetCodeRepository;
    @MockBean
    private MailService mailService;
    @MockBean
    private PasswordEncoder passwordEncoder;


    private MemberDto getMockGuestMemberDto() {
        return MemberDto.from(getMockGuestMember());
    }

    @Nested
    @DisplayName("회원가입 기능 테스트")
    class AboutCreateMember {

        private MemberCreateRequestDto getMockRequestDto() {
            MemberCreateRequestDto dto = new MemberCreateRequestDto();
            dto.setEmail("testGuest@test.com");
            dto.setMemberId("testGuest");
            dto.setPassword("password");
            return dto;
        }

        @Test
        @DisplayName("정상적인 요청이라면, 회원 가입시, 새로운 회원을 생성후 반환한다.")
        public void givenProperRequest_whenCreatingMember_thenCreatesNewMemberAndReturn() {

            // Given
            MemberCreateRequestDto mockRequestDto = getMockRequestDto();
            Member mockMember = getMockGuestMember();
            given(memberRepository.existsById(mockRequestDto.getMemberId())).willReturn(false);
            given(memberRepository.existsByEmail(mockRequestDto.getEmail())).willReturn(false);
            given(memberRepository.save(any(Member.class))).willReturn(mockMember);

            // When
            MemberCreateResponseDto responseDto = memberService.createMember(mockRequestDto);

            // Then
            then(memberRepository).should().existsById(mockRequestDto.getMemberId());
            then(memberRepository).should().existsByEmail(mockRequestDto.getEmail());
            then(memberRepository).should().save(any(Member.class));
            assertThat(responseDto)
                    .hasFieldOrPropertyWithValue("memberId", mockMember.getId())
                    .hasFieldOrPropertyWithValue("nickname", mockMember.getNickname())
                    .hasFieldOrPropertyWithValue("email", mockMember.getEmail());

        }

        @Test
        @DisplayName("중복된 아이디라면, 회원 가입시, 아이디 중복 예외를 반환한다.")
        public void givenDuplicateId_whenCreatingMember_thenReturnsDuplicateIdException() {

            // Given
            MemberCreateRequestDto mockRequestDto = getMockRequestDto();
            Member mockMember = getMockGuestMember();
            given(memberRepository.existsById(mockRequestDto.getMemberId())).willReturn(true);
            given(memberRepository.existsByEmail(mockRequestDto.getEmail())).willReturn(false);
            given(memberRepository.save(any(Member.class))).willReturn(mockMember);

            // When
            ApplicationException e =
                    assertThrows(ApplicationException.class, () -> memberService.createMember(mockRequestDto));

            // Then
            then(memberRepository).should().existsById(mockRequestDto.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertEquals(e.getErrorCode(), ErrorCode.DUPLICATE_ID);
        }

        @Test
        @DisplayName("중복된 이메일이라면, 회원 가입시, 이메일 중복 예외를 반환한다.")
        public void givenDuplicateEmail_whenCreatingMember_thenReturnsDuplicateIdException() {

            // Given
            MemberCreateRequestDto mockRequestDto = getMockRequestDto();
            Member mockMember = getMockGuestMember();
            given(memberRepository.existsById(mockRequestDto.getMemberId())).willReturn(false);
            given(memberRepository.existsByEmail(mockRequestDto.getEmail())).willReturn(true);
            given(memberRepository.save(any(Member.class))).willReturn(mockMember);

            // When
            ApplicationException e =
                    assertThrows(ApplicationException.class, () -> memberService.createMember(mockRequestDto));

            // Then
            then(memberRepository).should().existsById(mockRequestDto.getMemberId());
            then(memberRepository).should().existsByEmail(mockRequestDto.getEmail());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertEquals(e.getErrorCode(), ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("인증 메일 전송 기능 테스트")
    class AboutSendConfirmMail {

        @Test
        @DisplayName("정상적인 요청이라면, 인증 메일 전송 요청시, 이메일을 전송한다.")
        public void givenProperRequest_whenSendingConfirmMail_thenSendsConfirmMail() {
            // Given
            MemberDto mockMemberDto = getMockGuestMemberDto();

            // When
            memberService.sendConfirmMail(mockMemberDto);

            // Then
            then(emailConfirmCodeRepository).should().save(any(String.class), eq(mockMemberDto.getId()));
            then(mailService).should()
                    .sendEmail(eq(mockMemberDto.getEmail()), any(String.class), any(String.class), eq(true), eq(false));
        }
    }

    @Nested
    @DisplayName("이메일 인증 기능 테스트")
    class AboutConfirmEmail {

        @Test
        @DisplayName("정상적인 요청이라면, 메일 인증시, 회원 권한을 유저로 바꾼다.")
        public void givenProperRequest_whenConfirmingEmail_thenChangesToUserRole() {
            // Given
            String mockConfirmCode = UUID.randomUUID().toString();
            Member mockMember = getMockGuestMember();
            given(emailConfirmCodeRepository.findMemberIdByCode(mockConfirmCode)).willReturn(
                    Optional.of(mockMember.getId()));
            given(memberRepository.findById(mockMember.getId())).willReturn(Optional.of(mockMember));

            // When
            memberService.confirmEmail(mockConfirmCode);

            // Then
            then(emailConfirmCodeRepository).should().deleteByCode(mockConfirmCode);
        }

        @Test
        @DisplayName("유효하지 않은 코드라면, 메일 인증시, 예외를 발생시킨다.")
        public void givenInvalidConfirmCode_whenConfirmingEmail_thenThrowsApplicationException() {
            // Given
            String mockConfirmCode = UUID.randomUUID().toString();
            Member mockMember = getMockGuestMember();
            given(emailConfirmCodeRepository.findMemberIdByCode(mockConfirmCode)).willReturn(Optional.empty());
            given(memberRepository.findById(mockMember.getId())).willReturn(Optional.of(mockMember));

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> memberService.confirmEmail(mockConfirmCode));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.CODE_NOT_VALID);
            then(emailConfirmCodeRepository).should().findMemberIdByCode(any());
            then(emailConfirmCodeRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("조회한 유저가 이미 인증된 유저라면, 메일 인증시, 예외를 발생시킨다.")
        public void givenAlreadyConfirmedMember_whenConfirmingEmail_thenThrowsApplicationException() {
            // Given
            String mockConfirmCode = UUID.randomUUID().toString();
            Member mockMember = getMockUserMember();
            given(emailConfirmCodeRepository.findMemberIdByCode(mockConfirmCode)).willReturn(
                    Optional.of(mockMember.getId()));
            given(memberRepository.findById(mockMember.getId())).willReturn(Optional.of(mockMember));

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> memberService.confirmEmail(mockConfirmCode));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.EMAIL_ALREADY_CONFIRMED);
            then(emailConfirmCodeRepository).should().findMemberIdByCode(any());
            then(emailConfirmCodeRepository).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("유저 정보 조회 테스트")
    class AboutReadMember {

        @Test
        @DisplayName("조회할 유저 정보가 들어오면, 유저 정보 조회시, 기밀 정보가 제외된 정보를 반환한다.")
        public void givenMemberInfo_whenReadingMemberInfo_thenReturnsMemberInfoWithoutSecrets() {
            // Given
            Member mockMember = getMockUserMember();
            MemberDto mockMemberDto = MemberDto.from(mockMember);

            // When
            Object responseDto = memberService.readMember(mockMemberDto);

            // Then
            assertThat(responseDto)
                    .hasFieldOrPropertyWithValue("memberId", mockMember.getId())
                    .hasFieldOrPropertyWithValue("nickname", mockMember.getNickname())
                    .hasFieldOrPropertyWithValue("email", mockMember.getEmail());
        }
    }

    @Nested
    @DisplayName("유저 정보 수정 테스트")
    class AboutUpdateMember {

        @Test
        @DisplayName("정상적인 요청이라면, 유저 정보 수정시, 유저 정보가 수정된다.")
        public void givenProperRequest_whenUpdatingMemberInfo_thenUpdatesMemberInfo() {
            // Given
            Member mockMember = getMockUserMember();
            MemberDto mockMemberDto = MemberDto.from(mockMember);
            String newNickname = "changedNickname";
            MemberUpdateRequestDto requestDto = new MemberUpdateRequestDto();
            requestDto.setNickname(newNickname);
            given(memberRepository.findById(mockMember.getId())).willReturn(Optional.of(mockMember));

            // When
            memberService.updateMember(requestDto, mockMemberDto);

            // Then
            assertThat(mockMember)
                    .hasFieldOrPropertyWithValue("nickname", newNickname);
        }
    }

    @Nested
    @DisplayName("비밀번호 수정 테스트")
    class AboutUpdatePassword {

        @Test
        @DisplayName("정상적인 요청이라면, 비밀번호 수정시, 새로운 인코딩된 비밀번호로 수정된다.")
        public void givenProperRequest_whenUpdatingPassword_thenUpdatesPassword() {
            // Given
            String oldPassword = "oldPassword";
            String newPassword = "newPassword";
            String encodedNewPassword = "encodedNewPassword";
            Member mockMember = mock(Member.class);
            MemberDto mockMemberDto = MemberDto.from(getMockUserMember());
            PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto();
            requestDto.setOldPassword(oldPassword);
            requestDto.setNewPassword(newPassword);

            given(mockMember.getPw()).willReturn(oldPassword);
            given(memberRepository.findById(mockMemberDto.getId())).willReturn(Optional.of(mockMember));
            given(passwordEncoder.matches(eq(oldPassword), any(String.class))).willReturn(true);
            given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

            // When
            memberService.updatePassword(requestDto, mockMemberDto);

            // Then
            then(mockMember).should().updatePassword(encodedNewPassword);
        }

        @Test
        @DisplayName("기존 비밀번호를 잘못 입력했다면, 비밀번호 수정시, 예외를 발생시킨다.")
        public void givenWrongOldPassword_whenUpdatingPassword_thenThrowsApplicationException() {
            // Given
            String oldPassword = "wrongPassword";
            String newPassword = "changedPassword";
            String encodedNewPassword = "encodedNewPassword";
            Member mockMember = mock(Member.class);
            MemberDto mockMemberDto = MemberDto.from(getMockUserMember());
            PasswordUpdateRequestDto requestDto = new PasswordUpdateRequestDto();
            requestDto.setOldPassword(oldPassword);
            requestDto.setNewPassword(newPassword);

            given(mockMember.getPw()).willReturn(oldPassword);
            given(memberRepository.findById(mockMemberDto.getId())).willReturn(Optional.of(mockMember));
            given(passwordEncoder.matches(eq(oldPassword), any(String.class))).willReturn(false);
            given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> memberService.updatePassword(requestDto, mockMemberDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.INVALID_PASSWORD);
            then(mockMember).should().getPw();
            then(mockMember).shouldHaveNoMoreInteractions();

        }
    }

    @Nested
    @DisplayName("회원 삭제 테스트")
    class AboutDeleteMember {

        @Test
        @DisplayName("정상적인 요청이라면, 회원 삭제 시, 등록된 회원을 삭제한다.")
        public void givenProperRequest_whenDeletingMember_thenDeletesMember() {
            // Given
            Member mockMember = mock(Member.class);
            MemberDto mockMemberDto = MemberDto.from(getMockUserMember());
            given(memberRepository.findById(mockMemberDto.getId())).willReturn(Optional.of(mockMember));

            // When
            memberService.deleteMember(mockMemberDto);

            // Then
            then(memberRepository).should().delete(mockMember);
        }
    }

    @Nested
    @DisplayName("아이디 찾기 테스트")
    class AboutSendId {

        @Test
        @DisplayName("정상적인 요청이라면, 아이디 찾기 시, 아이디가 포함된 메일을 보낸다.")
        public void givenProperRequest_whenFindingId_thenSendsEmailWithId() {
            // Given
            Member mockMember = getMockUserMember();
            MemberFindIdRequestDto requestDto = new MemberFindIdRequestDto();
            requestDto.setEmail(mockMember.getEmail());
            given(memberRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(mockMember));

            // When
            memberService.sendId(requestDto);

            // Then
            then(mailService).should()
                    .sendEmail(eq(requestDto.getEmail()), any(String.class), any(String.class), eq(true), eq(false));
        }

        @Test
        @DisplayName("회원으로 등록된 이메일이 아니라면, 아이디 찾기 시, 예외를 발생시킨다.")
        public void givenInvalidEmail_whenFindingId_thenThrowsException() {
            // Given
            Member mockMember = getMockUserMember();
            MemberFindIdRequestDto requestDto = new MemberFindIdRequestDto();
            requestDto.setEmail(mockMember.getEmail());
            given(memberRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.empty());

            // When
            ApplicationException e = assertThrows(ApplicationException.class, () -> memberService.sendId(requestDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
            then(mailService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정 링크 발송 테스트")
    class AboutSendPasswordResetCode {

        @Test
        @DisplayName("정상적인 요청이라면, 비밀번호 재설정 링크 발송 시, 재설정 링크가 포함된 메일을 보낸다.")
        public void givenProperRequest_whenSendingPasswordResetLink_thenSendsEmailWithPasswordResetLink() {
            // Given
            Member mockMember = getMockUserMember();
            MemberSendResetCodeRequestDto requestDto = new MemberSendResetCodeRequestDto();
            requestDto.setEmail(mockMember.getEmail());
            requestDto.setMemberId(mockMember.getId());
            given(memberRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(mockMember));

            // When
            memberService.sendPasswordResetCode(requestDto);

            // Then
            then(mailService).should()
                    .sendEmail(eq(requestDto.getEmail()), any(String.class), any(String.class), eq(true), eq(false));
        }

        @Test
        @DisplayName("회원으로 등록된 이메일이 아니라면, 비밀번호 재설정 링크 발송 시, 예외를 발생시킨다.")
        public void givenInvalidEmail_whenSendingPasswordResetLink_thenThrowsApplicationException() {
            // Given
            Member mockMember = getMockUserMember();
            MemberSendResetCodeRequestDto requestDto = new MemberSendResetCodeRequestDto();
            requestDto.setEmail(mockMember.getEmail());
            requestDto.setMemberId(mockMember.getId());
            given(memberRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.empty());

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> memberService.sendPasswordResetCode(requestDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.USER_NOT_FOUND);
            then(mailService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("등록된 이메일과 아이디가 다르다면, 비밀번호 재설정 링크 발송 시, 예외를 발생시킨다.")
        public void givenUnmatchingId_whenSendingPasswordResetLink_thenThrowsApplicationException() {
            // Given
            String unmatchingId = "unmatchingId";
            Member mockMember = getMockUserMember();
            MemberSendResetCodeRequestDto requestDto = new MemberSendResetCodeRequestDto();
            requestDto.setEmail(mockMember.getEmail());
            requestDto.setMemberId(unmatchingId);
            given(memberRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(mockMember));

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> memberService.sendPasswordResetCode(requestDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.INVALID_ID);
            then(mailService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("비밀번호 재설정 테스트")
    class AboutResetPassword {

        @Test
        @DisplayName("정상적인 요청이라면, 비밀번호 재설정시, 비밀번호를 재설정한다.")
        public void givenProperRequest_whenResettingPassword_thenResetsPassword() {
            // Given
            String mockResetCode = "resetCode";
            String mockMemberId = "testUser";
            String newPassword = "newPassword";
            String encodedNewPassword = "encodedNewPassword";
            Member mockMember = mock(Member.class);
            PasswordResetRequestDto requestDto = new PasswordResetRequestDto();
            requestDto.setNewPassword(newPassword);
            requestDto.setResetCode(mockResetCode);

            given(mockMember.getId()).willReturn(mockMemberId);
            given(passwordResetCodeRepository.findMemberIdByCode(requestDto.getResetCode())).willReturn(
                    Optional.of(mockMemberId));
            given(memberRepository.findById(mockMemberId)).willReturn(Optional.of(mockMember));
            given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

            // When
            memberService.resetPassword(requestDto);

            // Then
            then(mockMember).should().updatePassword(encodedNewPassword);
            then(passwordResetCodeRepository).should().deleteByCode(requestDto.getResetCode());
        }


        @Test
        @DisplayName("유효하지 않은 재설정 코드라면, 비밀번호 재설정시, 예외를 발생시킨다.")
        public void givenInvalidResetCode_whenResettingPassword_thenThrowsApplicationException() {
            // Given
            String mockResetCode = "invalidResetCode";
            String mockMemberId = "testUser";
            String newPassword = "newPassword";
            String encodedNewPassword = "encodedNewPassword";
            Member mockMember = mock(Member.class);
            PasswordResetRequestDto requestDto = new PasswordResetRequestDto();
            requestDto.setNewPassword(newPassword);
            requestDto.setResetCode(mockResetCode);
            given(mockMember.getId()).willReturn(mockMemberId);
            given(passwordResetCodeRepository.findMemberIdByCode(requestDto.getResetCode())).willReturn(
                    Optional.empty());
            given(memberRepository.findById(mockMemberId)).willReturn(Optional.of(mockMember));
            given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

            // When
            ApplicationException e = assertThrows(ApplicationException.class,
                    () -> memberService.resetPassword(requestDto));

            // Then
            assertEquals(e.getErrorCode(), ErrorCode.CODE_NOT_VALID);
            then(mockMember).shouldHaveNoInteractions();
            then(passwordResetCodeRepository).should().findMemberIdByCode(any());
            then(passwordResetCodeRepository).shouldHaveNoMoreInteractions();
        }
    }
}