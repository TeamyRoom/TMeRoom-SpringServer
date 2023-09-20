package org.finalproject.tmeroom.member.controller;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.dto.request.MemberCreateRequestDto;
import org.finalproject.tmeroom.member.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public Response<Void> createMember(@RequestBody MemberCreateRequestDto requestDto) {
        memberService.createMember(requestDto);
        return Response.success();
    }

    @GetMapping("/email/confirm/resend")
    public Response<Void> resendConfirmMail(@AuthenticationPrincipal MemberDto memberDto) {
        memberService.sendConfirmMail(memberDto);
        return Response.success();
    }

    @PostMapping("/email/confirm/{confirmCode}")
    public Response<Void> confirmMail(@PathVariable String confirmCode) {
        memberService.confirmEmail(confirmCode);
        return Response.success();
    }

    // TODO: 프론트 없을 때 테스트 용도. 프론트 완료되면 삭제 예정
    @GetMapping("/email/confirm/{confirmCode}")
    public Response<Void> confirmMailTemp(@PathVariable String confirmCode) {
        memberService.confirmEmail(confirmCode);
        return Response.success();
    }
}
