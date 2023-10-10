package org.finalproject.tmeroom.admin.controller;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.admin.constant.LectureSearchType;
import org.finalproject.tmeroom.admin.constant.MemberRoleSearchType;
import org.finalproject.tmeroom.admin.constant.MemberSearchType;
import org.finalproject.tmeroom.admin.data.dto.request.*;
import org.finalproject.tmeroom.admin.data.dto.response.AdminLectureDetailReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminLecturePageReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberDetailReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberPageReadResponseDto;
import org.finalproject.tmeroom.admin.service.AdminService;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/members")
    public Response<AdminMemberPageReadResponseDto> searchMembers(@AuthenticationPrincipal MemberDto memberDto,
                                                                  @RequestParam MemberSearchType searchType,
                                                                  @RequestParam String keyword,
                                                                  @PageableDefault(
                                                                          direction = Sort.Direction.DESC,
                                                                          size = 20)
                                                                  Pageable pageable) {
        AdminMemberSearchRequestDto requestDto = AdminMemberSearchRequestDto.builder()
                .searchType(searchType)
                .keyword(keyword)
                .pageable(pageable)
                .build();

        AdminMemberPageReadResponseDto responseDto = adminService.searchMembers(requestDto, memberDto);
        return Response.success(responseDto);
    }

    @GetMapping("/lectures")
    public Response<AdminLecturePageReadResponseDto> searchLectures(@AuthenticationPrincipal MemberDto memberDto,
                                                                    @RequestParam LectureSearchType searchType,
                                                                    @RequestParam String keyword,
                                                                    @PageableDefault(
                                                                            direction = Sort.Direction.DESC,
                                                                            size = 20)
                                                                    Pageable pageable) {
        AdminLectureSearchRequestDto requestDto = AdminLectureSearchRequestDto.builder()
                .searchType(searchType)
                .keyword(keyword)
                .pageable(pageable)
                .build();

        AdminLecturePageReadResponseDto responseDto = adminService.searchLectures(requestDto, memberDto);
        return Response.success(responseDto);
    }

    @GetMapping("/member/{memberId}")
    public Response<AdminMemberDetailReadResponseDto> readMemberDetailProfile(
            @AuthenticationPrincipal MemberDto memberDto,
            @PathVariable String memberId) {
        AdminMemberDetailProfileRequestDto requestDto = AdminMemberDetailProfileRequestDto.builder()
                .memberId(memberId)
                .build();

        AdminMemberDetailReadResponseDto responseDto = adminService.readMemberDetailProfile(requestDto, memberDto);
        return Response.success(responseDto);
    }

    @GetMapping("/member/{memberId}/lectures")
    public Response<AdminLecturePageReadResponseDto> readMemberDetailLecture(
            @AuthenticationPrincipal MemberDto memberDto,
            @PathVariable String memberId,
            @RequestParam MemberRoleSearchType searchType,
            @PageableDefault(
                    direction = Sort.Direction.DESC,
                    size = 20)
            Pageable pageable) {
        AdminMemberDetailLectureRequestDto requestDto = AdminMemberDetailLectureRequestDto.builder()
                .searchType(searchType)
                .memberId(memberId)
                .pageable(pageable)
                .build();

        AdminLecturePageReadResponseDto responseDto = adminService.readMemberDetailLecture(requestDto, memberDto);
        return Response.success(responseDto);
    }

    @GetMapping("/lecture/{lectureCode}")
    public Response<AdminLectureDetailReadResponseDto> readLectureInfo(
            @AuthenticationPrincipal MemberDto memberDto,
            @PathVariable String lectureCode) {
        AdminLectureDetailRequestDto requestDto = AdminLectureDetailRequestDto.builder()
                .lectureCode(lectureCode)
                .build();

        AdminLectureDetailReadResponseDto responseDto = adminService.readLectureInfo(requestDto, memberDto);
        return Response.success(responseDto);
    }

    @DeleteMapping("/member/{memberId}")
    public Response<Void> deleteMember(@AuthenticationPrincipal MemberDto memberDto,
                                       @PathVariable String memberId) {
        adminService.deleteMember(memberId, memberDto);
        return Response.success();
    }

    @DeleteMapping("/lecture/{lectureCode}")
    public Response<Void> deleteLecture(@AuthenticationPrincipal MemberDto memberDto,
                                        @PathVariable String lectureCode) {
        adminService.deleteMember(lectureCode, memberDto);
        return Response.success();
    }
}
