package org.finalproject.tmeroom.comment.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.comment.data.dto.request.CommentCreateRequestDto;
import org.finalproject.tmeroom.comment.data.dto.request.CommentUpdateRequestDto;
import org.finalproject.tmeroom.comment.data.dto.response.CommentDetailResponseDto;
import org.finalproject.tmeroom.comment.data.entity.Comment;
import org.finalproject.tmeroom.comment.repository.CommentRepository;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.finalproject.tmeroom.question.data.entity.Question;
import org.finalproject.tmeroom.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final QuestionRepository questionRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    // 댓글 조회
    public Page<CommentDetailResponseDto> readComments(Long questionId, Pageable pageable) {
        Question question = questionRepository.getReferenceById(questionId);
        Page<Comment> commentPage = commentRepository.findByQuestion(pageable, question);

        return commentPage.map(CommentDetailResponseDto::from);
    }

    // 댓글 게시
    public void createComment(Long questionId, CommentCreateRequestDto requestDto, MemberDto memberDto) {
        Question question = questionRepository.getReferenceById(questionId);
        Member commenter = memberRepository.getReferenceById(memberDto.getId());
        Comment comment = requestDto.toEntity(commenter, question);

        commentRepository.save(comment);
    }

    // 댓글 수정
    public void updateComment(Long commentId, CommentUpdateRequestDto requestDto, MemberDto memberDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_COMMENT_ID));

        checkPermission(comment, memberDto);

        comment.update(requestDto);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, MemberDto memberDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_COMMENT_ID));
        checkPermission(comment, memberDto);

        commentRepository.delete(comment);
    }

    private void checkPermission(Comment comment, MemberDto memberDto) {
        if (memberDto == null || !Objects.equals(comment.getCommenter().getId(), memberDto.getId())) {
            throw new ApplicationException(ErrorCode.INVALID_PERMISSION);
        }
    }
}
