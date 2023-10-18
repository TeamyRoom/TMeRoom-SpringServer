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
@RequestMapping("/api/v1")
public class EchoController {

    @GetMapping("/echo")
    public Response<Void> test() {
        return Response.success();
    }
}
