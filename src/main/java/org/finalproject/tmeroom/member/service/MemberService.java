package org.finalproject.tmeroom.member.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.common.service.MailService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.dto.request.MemberCreateRequestDto;
import org.finalproject.tmeroom.member.data.dto.response.MemberCreateResponseDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.EmailConfirmRepository;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-19
 * 다음과 같은 회원 관련 로직을 포함
 * 회원 가입, 이메일 인증
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final EmailConfirmRepository emailConfirmRepository;
    private final MailService mailService;

    public MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto) {

        checkIdDuplicate(requestDto.getMemberId());
        checkEmailDuplicate(requestDto.getEmail());
        Member savedMember = memberRepository.save(requestDto.toEntity(passwordEncoder));
        sendConfirmMail(MemberDto.from(savedMember));
        return MemberCreateResponseDto.from(savedMember);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Redis에 인증 메일 정보 저장을 실패했다고 회원 가입까지 롤백되면 안됨
    public void sendConfirmMail(MemberDto memberDto) {

        String confirmCode = UUID.randomUUID().toString();
        emailConfirmRepository.save(confirmCode, memberDto.getId());

        String subject = "[티미룸] 인증 링크를 발송해 드립니다.";
        String content = getConfirmMailContent(confirmCode);
        mailService.sendEmail(memberDto.getEmail(), subject, content, true, false);
    }

    public void confirmEmail(String confirmCode) {

        String memberId = emailConfirmRepository.findMemberIdByConfirmCode(confirmCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.CONFIRM_CODE_NOT_VALID));

        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        foundMember.confirmEmail();

        emailConfirmRepository.deleteByConfirmCode(confirmCode);

    }

    // TODO: 요청을 백엔드로 직접 보내도록 했는데, 프론트가 짜여지면 링크를 수정해야 함.
    private String getConfirmMailContent(String confirmCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin:20px;'>");
        sb.append("<p>티미룸 인증 링크를 발송해 드립니다.</p>");
        sb.append("<br>");
        sb.append("<div align='center' style='border:1px solid black; font-family:verdana'>");
        sb.append("<h3>인증 링크</h3>");
        sb.append("<div style='font-size:130%'>");
        sb.append("<strong>");
        sb.append("localhost:8080/api/v1/member/confirm/" + confirmCode + "</strong></div><br/>");
        sb.append("</div></br></br>");
        sb.append("<p>감사합니다.</p>");
        sb.append("</div>");
        return sb.toString();
    }

    private void checkIdDuplicate(String id) {
        if (memberRepository.existsById(id)) {
            throw new ApplicationException(ErrorCode.DUPLICATE_ID);
        }
    }

    private void checkEmailDuplicate(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
