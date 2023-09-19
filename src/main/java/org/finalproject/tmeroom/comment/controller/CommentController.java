package org.finalproject.tmeroom.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.comment.data.dto.request.CommentCreateRequestDto;
import org.finalproject.tmeroom.comment.data.dto.request.CommentUpdateRequestDto;
import org.finalproject.tmeroom.comment.data.dto.response.CommentDetailResponseDto;
import org.finalproject.tmeroom.comment.service.CommentService;
import org.finalproject.tmeroom.common.data.dto.Response;
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
public class CommentController {
    private final CommentService commentService;

    // 댓글 조회
    @GetMapping("/lecture/{lectureCode}/question/{questionId}/comments")
    public Response<Page<CommentDetailResponseDto>> readComments(@PathVariable Long lectureCode, @PathVariable Long questionId, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<CommentDetailResponseDto> dtoList = commentService.readComments(questionId, pageable);

        return Response.success(dtoList);
    }

    // 댓글 게시
    @PostMapping("/lecture/{lectureCode}/question/{questionId}/comment")
    public Response<Void> createComment(@PathVariable String lectureCode, @PathVariable Long questionId, @AuthenticationPrincipal MemberDto memberDto, @RequestBody @Valid CommentCreateRequestDto commentCreateRequestDto) {
        commentService.createComment(questionId, commentCreateRequestDto, memberDto);

        return Response.success();
    }

    // 댓글 수정
    @PutMapping("/lecture/{lectureCode}/question/{questionId}/comment/{commentId}")
    public Response<Void> updateComment(@PathVariable String lectureCode, @PathVariable Long questionId, @PathVariable Long commentId, @AuthenticationPrincipal MemberDto memberDto, @RequestBody @Valid CommentUpdateRequestDto requestDto) {
        commentService.updateComment(commentId, requestDto, memberDto);

        return Response.success();
    }

    // 댓글 삭제
    @DeleteMapping("/lecture/{lectureCode}/question/{questionId}/comment/{commentId}")
    public Response<Void> deleteComment(@PathVariable String lectureCode, @PathVariable Long questionId, @PathVariable Long commentId, @AuthenticationPrincipal MemberDto memberDto) {
        commentService.deleteComment(commentId, memberDto);

        return Response.success();
    }
}
