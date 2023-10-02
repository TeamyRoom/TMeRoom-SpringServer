package org.finalproject.tmeroom.member.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.common.service.MailService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.dto.request.*;
import org.finalproject.tmeroom.member.data.dto.response.MemberCreateResponseDto;
import org.finalproject.tmeroom.member.data.dto.response.ReadMemberResponseDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.EmailConfirmCodeRepository;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.finalproject.tmeroom.member.repository.PasswordResetCodeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-19
 * 다음과 같은 회원 관련 로직을 포함
 * 회원 가입, 이메일 인증, 아이디 및 이메일 중복 확인,
 * 회원 관련 CRUD, 아이디 확인 및 비밀번호 재설정 이메일 전송
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final EmailConfirmCodeRepository emailConfirmCodeRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final MailService mailService;

    public MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto) {

        if (isIdDuplicate(requestDto.getMemberId())) {
            throw new ApplicationException(ErrorCode.DUPLICATE_ID);
        }

        if (isEmailDuplicate(requestDto.getEmail())) {
            throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member savedMember = memberRepository.save(requestDto.toEntity());
        sendConfirmMail(MemberDto.from(savedMember));
        return MemberCreateResponseDto.from(savedMember);
    }

    public boolean isIdDuplicate(String id) {
        return memberRepository.existsById(id);
    }

    public boolean isEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Redis에 인증 메일 정보 저장을 실패했다고 회원 가입까지 롤백되면 안됨
    public void sendConfirmMail(MemberDto memberDto) {

        String confirmCode = UUID.randomUUID().toString();
        emailConfirmCodeRepository.save(confirmCode, memberDto.getId());

        String subject = "[티미룸] 인증 링크를 발송해 드립니다.";
        String content = getConfirmMailContent(confirmCode);
        mailService.sendEmail(memberDto.getEmail(), subject, content, true, false);
    }

    public void confirmEmail(String confirmCode) {

        String memberId = emailConfirmCodeRepository.findMemberIdByCode(confirmCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.CODE_NOT_VALID));

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        foundMember.confirmEmail();

        emailConfirmCodeRepository.deleteByCode(confirmCode);
    }

    public ReadMemberResponseDto readMember(MemberDto memberDto) {
        return ReadMemberResponseDto.from(memberDto);
    }

    public void updateMember(MemberUpdateRequestDto requestDto, MemberDto memberDto) {
        Member foundMember = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        foundMember.updateInfo(requestDto);
    }

    public void updatePassword(PasswordUpdateRequestDto requestDto, MemberDto memberDto) {

        Member foundMember = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(foundMember.getPw(), requestDto.getOldPassword())) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        foundMember.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    public void deleteMember(MemberDto memberDto) {

        Member foundMember = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        memberRepository.delete(foundMember);
    }

    public void sendId(MemberFindIdRequestDto requestDto) {

        Member foundMember = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        String subject = "[티미룸] 아이디 찾기 결과를 보내드립니다.";
        String content = getFindIdMailContent(foundMember.getId());
        mailService.sendEmail(foundMember.getEmail(), subject, content, true, false);
    }

    public void sendPasswordResetCode(MemberSendResetCodeRequestDto requestDto) {

        Member foundMember = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        if (!foundMember.isIdMatch(requestDto.getMemberId())) {
            throw new ApplicationException(ErrorCode.INVALID_ID);
        }

        String resetCode = UUID.randomUUID().toString();
        passwordResetCodeRepository.save(resetCode, foundMember.getId());

        String subject = "[티미룸] 비밀번호 재설정 링크를 보내드립니다.";
        String content = getResetPasswordMailContent(resetCode);
        mailService.sendEmail(foundMember.getEmail(), subject, content, true, false);
    }

    public void resetPassword(PasswordResetRequestDto requestDto) {

        String memberId = passwordResetCodeRepository.findMemberIdByCode(requestDto.getResetCode())
                .orElseThrow(() -> new ApplicationException(ErrorCode.CODE_NOT_VALID));

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        foundMember.updatePassword(passwordEncoder.encode(requestDto.getNewPassword()));

        passwordResetCodeRepository.deleteByCode(requestDto.getResetCode());
    }

    // TODO: 요청을 백엔드로 직접 보내도록 했는데, 프론트가 짜여지면 링크를 수정해야 함.
    private String getConfirmMailContent(String confirmCode) {
        String sb = "<div style='margin:20px;'>" +
                "<p>티미룸 인증 링크를 보내드립니다.</p>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana'>" +
                "<h3>인증 링크</h3>" +
                "<div style='font-size:130%'>" +
                "<strong>" +
                "localhost:8080/api/v1/email/member/confirm/" + confirmCode +
                "</strong></div><br/></div></br></br>" +
                "<p>감사합니다.</p>" +
                "</div>";
        return sb;
    }

    private String getFindIdMailContent(String memberId) {
        String sb = "<div style='margin:20px;'>" +
                "<p>요청하신 티미룸 아이디 찾기 결과입니다.</p>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana'>" +
                "<h3>아이디</h3>" +
                "<div style='font-size:130%'>" +
                "<strong>" +
                memberId +
                "</strong></div><br/></div></br></br>" +
                "<p>감사합니다.</p>" +
                "</div>";
        return sb;
    }

    // TODO: 요청을 백엔드로 직접 보내도록 했는데, 프론트가 짜여지면 링크를 수정해야 함.
    private String getResetPasswordMailContent(String resetCode) {
        String sb = "<div style='margin:20px;'>" +
                "<p>요청하신 티미룸 비밀번호 재설정 링크입니다.</p>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana'>" +
                "<h3>비밀번호 재설정 링크</h3>" +
                "<div style='font-size:130%'>" +
                "<strong>" +
                "localhost:8080/api/v1/member/lost/password/" + resetCode +
                "</strong></div><br/></div></br></br>" +
                "<p>감사합니다.</p>" +
                "</div>";
        return sb;
    }
}
