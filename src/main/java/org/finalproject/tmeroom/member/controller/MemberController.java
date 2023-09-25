package org.finalproject.tmeroom.member.controller;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.dto.request.*;
import org.finalproject.tmeroom.member.data.dto.response.ReadMemberResponseDto;
import org.finalproject.tmeroom.member.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/id/duplicate/{memberId}")
    public Response<Boolean> isIdDuplicate(@PathVariable String memberId) {
        boolean isIdDuplicate = memberService.isIdDuplicate(memberId);
        return Response.success(isIdDuplicate);
    }

    @GetMapping("/email/duplicate/{memberEmail}")
    public Response<Boolean> isEmailDuplicate(@PathVariable String memberEmail) {
        boolean isEmailDuplicate = memberService.isEmailDuplicate(memberEmail);
        return Response.success(isEmailDuplicate);
    }

    @GetMapping("/email/confirm/resend")
    public Response<Void> resendConfirmMail(@AuthenticationPrincipal MemberDto memberDto) {
        memberService.sendConfirmMail(memberDto);
        return Response.success();
    }

    @PutMapping("/email/confirm/{confirmCode}")
    public Response<Void> confirmMail(@PathVariable String confirmCode) {
        memberService.confirmEmail(confirmCode);
        return Response.success();
    }

    @GetMapping
    public Response<ReadMemberResponseDto> getProfile(@AuthenticationPrincipal MemberDto memberDto) {
        ReadMemberResponseDto responseDto = memberService.readMember(memberDto);
        return Response.success(responseDto);
    }

    @PutMapping
    public Response<Void> updateProfile(@RequestBody MemberUpdateRequestDto requestDto, @AuthenticationPrincipal MemberDto memberDto) {
        memberService.updateMember(requestDto, memberDto);
        return Response.success();
    }

    @PutMapping("/password")
    public Response<Void> updatePassword(@RequestBody PasswordUpdateRequestDto requestDto, @AuthenticationPrincipal MemberDto memberDto) {
        memberService.updatePassword(requestDto, memberDto);
        return Response.success();
    }

//    @GetMapping("/{memberEmail}")
//    public Response<MemberSearchResponseDto> findMember(@PathVariable String memberEmail) {
//        MemberSearchResponseDto responseDto = memberService.searchMember(memberEmail);
//        return Response.success(responseDto);
//    }

    @DeleteMapping
    public Response<Void> deleteMember(@AuthenticationPrincipal MemberDto memberDto) {
        memberService.deleteMember(memberDto);
        return Response.success();
    }

    @GetMapping("/id/lost")
    public Response<Void> sendLostId(@RequestParam String email) {
        MemberFindIdRequestDto requestDto= new MemberFindIdRequestDto();
        requestDto.setEmail(email);
        memberService.sendId(requestDto);
        return Response.success();
    }

    @PostMapping("/password/lost")
    public Response<Void> sendPasswordResetCode(@RequestBody MemberSendResetCodeRequestDto requestDto) {
        memberService.sendPasswordResetCode(requestDto);
        return Response.success();
    }

    @PutMapping("/password/lost")
    public Response<Void> resetPassword(@RequestBody PasswordResetRequestDto requestDto) {
        memberService.resetPassword(requestDto);
        return Response.success();
    }
}
