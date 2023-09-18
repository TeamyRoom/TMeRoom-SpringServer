package org.finalproject.tmeroom.member.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.data.dto.request.MemberCreateRequestDto;
import org.finalproject.tmeroom.member.data.dto.response.MemberCreateResponseDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public MemberCreateResponseDto createMember(MemberCreateRequestDto requestDto) {

        checkIdDuplicate(requestDto.getMemberId());
        checkEmailDuplicate(requestDto.getEmail());
        Member savedMember = memberRepository.save(requestDto.toEntity(passwordEncoder));
        return MemberCreateResponseDto.from(savedMember);
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
