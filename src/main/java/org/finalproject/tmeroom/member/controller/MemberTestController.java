package org.finalproject.tmeroom.member.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.member.data.dto.request.MemberCreateRequestDto;
import org.finalproject.tmeroom.member.data.dto.request.PasswordResetRequestDto;
import org.finalproject.tmeroom.member.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-24
 * 프론트 없는 동안 필요한 테스트용 컨트롤러
 * 프론트가 완성되면 모두 삭제 예정
 */
//TODO: 프론트 완성 시에 삭제 필요
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberTestController {

    private final MemberService memberService;

    @PostConstruct
    private void makeTesterMember() {
        MemberCreateRequestDto requestDto = new MemberCreateRequestDto();
        requestDto.setMemberId("tester00");
        requestDto.setNickname("tester");
        requestDto.setPassword("test");
        requestDto.setEmail("tester@test.com");
        memberService.createMember(requestDto);
    }

    @GetMapping("/email/confirm/{confirmCode}")
    public Response<Void> confirmMailTemp(@PathVariable String confirmCode) {
        memberService.confirmEmail(confirmCode);
        return Response.success();
    }

    @GetMapping("/password/lost/{resetCode}")
    public Response<Void> resetPasswordTemp(@PathVariable String resetCode) {
        PasswordResetRequestDto requestDto = new PasswordResetRequestDto();
        requestDto.setResetCode(resetCode);
        memberService.resetPassword(requestDto);
        return Response.success();
    }
}
