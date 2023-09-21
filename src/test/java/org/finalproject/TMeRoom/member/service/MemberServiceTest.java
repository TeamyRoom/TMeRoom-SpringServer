package org.finalproject.TMeRoom.member.service;

import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.dto.request.MemberCreateRequestDto;
import org.finalproject.tmeroom.member.data.dto.response.MemberCreateResponseDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.finalproject.tmeroom.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest(classes = {MemberService.class})
@DisplayName("인증 서비스 로직 테스트")
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private MemberCreateRequestDto getMockRequestDto() {
        MemberCreateRequestDto dto = new MemberCreateRequestDto();
        dto.setEmail("testGuest@test.com");
        dto.setMemberId("testGuest");
        dto.setPassword("password");
        return dto;
    }

    private Member getMockGuestMember() {
        return Member.builder()
                .id("testGuest")
                .pw("encodedPw")
                .email("testGuest@test.com")
                .nickname("testGuest")
                .role(MemberRole.GUEST)
                .build();
    }

    @Nested
    @DisplayName("회원가입 기능 테스트")
    class AboutCreateMember {

        @Test
        @DisplayName("정상적인 요청이라면, 회원 가입시, 새로운 회원을 생성후 반환한다.")
        public void givenProperRequest_whenCreatingMember_thenCreatesNewMemberAndReturn() {

            // Given
            MemberCreateRequestDto mockRequestDto = getMockRequestDto();
            Member mockMember = getMockGuestMember();
            given(memberRepository.existsById(mockRequestDto.getMemberId())).willReturn(false);
            given(memberRepository.existsByEmail(mockRequestDto.getEmail())).willReturn(false);
            given(memberRepository.save(any(Member.class))).willReturn(mockMember);
            given(passwordEncoder.encode(mockRequestDto.getPassword())).willReturn(mockMember.getPw());

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
            given(passwordEncoder.encode(mockRequestDto.getPassword())).willReturn(mockMember.getPw());

            // When
            ApplicationException e = assertThrows(ApplicationException.class, () -> memberService.createMember(mockRequestDto));

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
            given(passwordEncoder.encode(mockRequestDto.getPassword())).willReturn(mockMember.getPw());

            // When
            ApplicationException e = assertThrows(ApplicationException.class, () -> memberService.createMember(mockRequestDto));

            // Then
            then(memberRepository).should().existsById(mockRequestDto.getMemberId());
            then(memberRepository).should().existsByEmail(mockRequestDto.getEmail());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertEquals(e.getErrorCode(), ErrorCode.DUPLICATE_EMAIL);
        }
    }
}