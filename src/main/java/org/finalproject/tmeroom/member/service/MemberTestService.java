package org.finalproject.tmeroom.member.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.dto.request.MemberCreateRequestDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberTestService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createTestAdmin() {

        memberRepository.save(
                Member.builder()
                        .id("admin")
                        .pw(passwordEncoder.encode("admin"))
                        .nickname("어드민")
                        .email("testAdmin@test.com")
                        .role(MemberRole.ADMIN)
                        .build()
        );
    }

}
