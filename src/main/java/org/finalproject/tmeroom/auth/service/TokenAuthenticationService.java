package org.finalproject.tmeroom.auth.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-13
 * JWT의 Subject로 회원 정보를 찾아 주는 서비스 로직
 */
@Service
@RequiredArgsConstructor
public class TokenAuthenticationService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberDto getMemberDto(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        return MemberDto.from(member);
    }
}
