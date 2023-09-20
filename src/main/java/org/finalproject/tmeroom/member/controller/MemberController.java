package org.finalproject.tmeroom.member.controller;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.member.data.dto.request.EmailConfirmRequestDto;
import org.finalproject.tmeroom.member.data.dto.request.MemberCreateRequestDto;
import org.finalproject.tmeroom.member.service.MemberService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/email/confirm")
    public Response<Void> confirmMailTemp(@RequestBody EmailConfirmRequestDto requestDto) {
        memberService.confirmEmail(requestDto.getConfirmCode());
        return Response.success();
    }

    // TODO: 프론트 없을 때 테스트 용도. 프론트 완료되면 삭제 예정
    @GetMapping("/confirm/{confirm-code}")
    public Response<Void> confirmMailTemp(@PathVariable("confirm-code") String confirmCode) {
        memberService.confirmEmail(confirmCode);
        return Response.success();
    }
}
