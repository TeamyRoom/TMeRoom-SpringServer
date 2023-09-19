package org.finalproject.tmeroom.question.controller;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.question.data.dto.request.QuestionCreateRequestDto;
import org.finalproject.tmeroom.question.data.dto.request.QuestionUpdateRequestDto;
import org.finalproject.tmeroom.question.data.dto.response.QuestionDetailResponseDto;
import org.finalproject.tmeroom.question.data.dto.response.QuestionListResponseDto;
import org.finalproject.tmeroom.question.service.QuestionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class questionController {
    private final QuestionService questionService;

    // 질문 목록 조회(GET)
    @GetMapping("/lecture/{lectureCode}/questions")
    public Response<Page<QuestionListResponseDto>> lookupQuestions(@PathVariable String lectureCode, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<QuestionListResponseDto> dtoList = questionService.lookupQuestions(lectureCode, pageable);
        return Response.success(dtoList);
    }

    // 질문 단일 조회(GET)
    @GetMapping("/lecture/{lectureCode}/question/{questionId}")
    public Response<QuestionDetailResponseDto> readQuestion(@PathVariable String lectureCode, @PathVariable Long questionId, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        QuestionDetailResponseDto dto = questionService.readQuestion(questionId);
        return Response.success(dto);
    }

    // 질문 게시(POST)
    @PostMapping("/lecture/{lectureCode}/question")
    public Response<Void> createQuestion(@PathVariable String lectureCode, @AuthenticationPrincipal MemberDto memberDto, QuestionCreateRequestDto requestDto) {
        questionService.createQuestion(lectureCode, memberDto, requestDto);
        return Response.success();
    }

    // 질문 수정(PUT)
    @PutMapping("/lecture/{lectureCode}/question/{questionId}")
    public Response<Void> updateQuestion(@PathVariable String lectureCode, @PathVariable Long questionId, @AuthenticationPrincipal MemberDto memberDto, QuestionUpdateRequestDto requestDto) {
        questionService.updateQuestion(questionId, memberDto, requestDto);
        return Response.success();
    }

    // 질문 삭제(DELETE)
    @DeleteMapping("/lecture/{lectureCode}/question/{questionId}")
    public Response<Void> deleteQuestion(@PathVariable String lectureCode, @PathVariable Long questionId, @AuthenticationPrincipal MemberDto memberDto) {
        questionService.deleteQuestion(questionId, memberDto);
        return Response.success();
    }

    // 질문 공개 여부 수정(PUT)
    @PutMapping("/lecture/{lectureCode}/question/{questionId}/public")
    public Response<Void> openQuestion(@PathVariable String lectureCode, @PathVariable Long questionId, @AuthenticationPrincipal MemberDto memberDto) {
        questionService.openQuestion(questionId, memberDto);
        return Response.success();

    }
}
