package org.finalproject.tmeroom.lecture.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.lecture.data.dto.request.AppointTeacherRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.request.LectureCreateRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.request.LectureUpdateRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.response.*;
import org.finalproject.tmeroom.lecture.service.LectureService;
import org.finalproject.tmeroom.lecture.service.StudentService;
import org.finalproject.tmeroom.lecture.service.TeacherService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    @PostMapping("/lecture")
    public Response<LectureCreateResponseDto> createLecture(
            @RequestBody @Valid LectureCreateRequestDto lectureCreateRequestDto,
            @AuthenticationPrincipal MemberDto memberDto) {
        lectureCreateRequestDto.setMemberDTO(memberDto);
        LectureCreateResponseDto lectureCreateResponseDto = lectureService.createLecture(lectureCreateRequestDto);
        return Response.success(lectureCreateResponseDto);
    }

    @GetMapping("/lecture/{lectureCode}")
    public Response<LectureAccessResponseDTO> accessLecture(@PathVariable String lectureCode, @AuthenticationPrincipal MemberDto memberDto) {
        LectureAccessResponseDTO lectureAccessResponseDTO = lectureService.accessLecture(lectureCode, memberDto.getId());
        return Response.success(lectureAccessResponseDTO);
    }

    @PutMapping("/lecture/{lectureCode}")
    public Response<Void> updateLecture(@PathVariable String lectureCode, @AuthenticationPrincipal MemberDto memberDto,
                                        @RequestBody @Valid LectureUpdateRequestDto requestDto) {
        requestDto.setLectureCode(lectureCode);
        requestDto.setMemberDTO(memberDto);
        lectureService.updateLecture(requestDto);
        return Response.success();
    }

    @DeleteMapping("/lecture/{lectureCode}")
    public Response<Void> deleteLecture(@PathVariable String lectureCode,
                                        @AuthenticationPrincipal MemberDto memberDto) {
        lectureService.deleteLecture(lectureCode, memberDto);
        return Response.success();
    }

    // 강의에 임명된 강사 조회
    @GetMapping("/lecture/{lectureCode}/teachers")
    public Response<Page<TeacherDetailResponseDto>> readTeachers(@PathVariable String lectureCode,
                                                                 @AuthenticationPrincipal MemberDto memberDto,
                                                                 @PageableDefault(sort = "createdAt",
                                                                         direction = Sort.Direction.DESC)
                                                                 Pageable pageable) {
        Page<TeacherDetailResponseDto> dtoList = teacherService.lookupTeachers(lectureCode, memberDto, pageable);
        return Response.success(dtoList);
    }

    // 강의 강사 임명
    @PostMapping("/lecture/{lectureCode}/teacher")
    public Response<Void> appointTeacher(@PathVariable String lectureCode, @AuthenticationPrincipal MemberDto memberDto,
                                         @RequestBody @Valid AppointTeacherRequestDto requestDto) {
        teacherService.appointTeacher(lectureCode, memberDto, requestDto);
        return Response.success();
    }

    // 강사 수락
    @PutMapping("/lecture/{lectureCode}/teacher")
    public Response<Void> acceptTeacher(@PathVariable String lectureCode,
                                        @AuthenticationPrincipal MemberDto memberDto) {
        teacherService.acceptTeacher(lectureCode, memberDto);
        return Response.success();
    }

    // 강사 거부
    @DeleteMapping("/lecture/{lectureCode}/teacher")
    public Response<Void> rejectTeacher(@PathVariable String lectureCode,
                                        @AuthenticationPrincipal MemberDto memberDto) {
        teacherService.rejectTeacher(lectureCode, memberDto);
        return Response.success();
    }

    // 강의 강사 해임
    @DeleteMapping("/lecture/{lectureCode}/teacher/{teacherId}")
    public Response<Void> dismissTeacher(@PathVariable String lectureCode, @PathVariable String teacherId,
                                         @AuthenticationPrincipal MemberDto memberDto) {
        teacherService.dismissTeacher(lectureCode, teacherId, memberDto);
        return Response.success();
    }

    // 수강 중인 강의 조회
    @GetMapping("/lectures/taking")
    public Response<Page<LectureDetailResponseDto>> lookupMyLectures(@AuthenticationPrincipal MemberDto memberDto,
                                                                     @PageableDefault(sort = "appliedAt",
                                                                             direction = Sort.Direction.DESC)
                                                                     Pageable pageable) {
        Page<LectureDetailResponseDto> dtoList = studentService.lookupMyLectures(memberDto, pageable);
        return Response.success(dtoList);
    }

    // 수강 신청
    @PostMapping("/lecture/{lectureCode}/application")
    public Response<Void> applyLecture(@PathVariable String lectureCode, @AuthenticationPrincipal MemberDto memberDto) {
        studentService.applyLecture(lectureCode, memberDto);
        return Response.success();
    }

    // 수강 신청 철회
    @DeleteMapping("/lecture/{lectureCode}/application")
    public Response<Void> cancelApplication(@PathVariable String lectureCode,
                                            @AuthenticationPrincipal MemberDto memberDto) {
        studentService.cancelApplication(lectureCode, memberDto);
        return Response.success();
    }

    // 수강 신청 인원 목록 조회
    @GetMapping("/lecture/{lectureCode}/applications")
    public Response<Page<StudentDetailResponseDto>> readStudents(@PathVariable String lectureCode,
                                                                 @AuthenticationPrincipal MemberDto memberDto,
                                                                 @PageableDefault(sort = "appliedAt",
                                                                         direction = Sort.Direction.DESC)
                                                                 Pageable pageable) {
        Page<StudentDetailResponseDto> dtoList = studentService.checkApplicants(lectureCode, memberDto, pageable);
        return Response.success(dtoList);
    }

    // 수강 신청 수락
    @PutMapping("/lecture/{lectureCode}/application/{applicantId}")
    public Response<Void> acceptApplicant(@PathVariable String lectureCode, @PathVariable String applicantId,
                                          @AuthenticationPrincipal MemberDto memberDto) {
        studentService.acceptApplicant(lectureCode, applicantId, memberDto);
        return Response.success();
    }

    // 수강 신청 반려
    @DeleteMapping("/lecture/{lectureCode}/application/{applicantId}")
    public Response<Void> rejectApplicant(@PathVariable String lectureCode, @PathVariable String applicantId,
                                          @AuthenticationPrincipal MemberDto memberDto) {
        studentService.rejectApplicant(lectureCode, applicantId, memberDto);
        return Response.success();
    }
}
