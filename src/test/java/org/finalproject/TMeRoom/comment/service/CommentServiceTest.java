package org.finalproject.TMeRoom.comment.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.finalproject.TMeRoom.common.util.MockMemberProvider;
import org.finalproject.tmeroom.comment.data.dto.request.CommentCreateRequestDto;
import org.finalproject.tmeroom.comment.data.dto.request.CommentUpdateRequestDto;
import org.finalproject.tmeroom.comment.data.dto.response.CommentDetailResponseDto;
import org.finalproject.tmeroom.comment.data.entity.Comment;
import org.finalproject.tmeroom.comment.repository.CommentRepository;
import org.finalproject.tmeroom.comment.service.CommentService;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.finalproject.tmeroom.question.data.entity.Question;
import org.finalproject.tmeroom.question.repository.QuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.finalproject.TMeRoom.common.util.MockMemberProvider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest(classes = {CommentService.class})
@Import(value = MockMemberProvider.class)
@ActiveProfiles("test")
@DisplayName("댓글 서비스")
class CommentServiceTest {
    @Autowired
    private CommentService commentService;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private QuestionRepository questionRepository;
    @MockBean
    private MemberRepository memberRepository;

    public Lecture getMockLecture() {
        return Lecture.builder()
                .manager(getMockManagerMember())
                .lectureName("강의명")
                .lectureCode("code")
                .build();
    }

    public Question getMockQuestion() {
        return Question.builder()
                .id(1l)
                .lecture(getMockLecture())
                .author(getMockStudentMember())
                .title("title")
                .content("content")
                .authorNickname("author")
                .isPublic(false)
                .build();
    }

    public Comment getMockComment(){
        return Comment.builder()
                .id(1l)
                .commenter(getMockUserMember())
                .question(getMockQuestion())
                .content("content")
                .build();
    }

    @Nested
    @DisplayName("댓글 조회")
    class readComments{
        @Test
        @DisplayName("댓글 조회 요청시 댓글 목록을 반환한다.")
        void success_return_comments() {
            // Given
            Long questionId = 1l;
            Pageable pageable = PageRequest.of(0, 20);
            Question question = getMockQuestion();
            Comment returnComment = getMockComment();
            Page<Comment> questionPage = new PageImpl<>(List.of(returnComment), pageable, 0);

            given(questionRepository.getReferenceById(questionId)).willReturn(question);
            given(commentRepository.findByQuestion(question, pageable)).willReturn(questionPage);

            // When
            Page<CommentDetailResponseDto> responseDtoPage = commentService.readComments(questionId, pageable);

            // Then
            assertThat(responseDtoPage.get().findFirst().get().getCommentId()).isEqualTo(
                    returnComment.getId());
            assertThat(responseDtoPage.get().findFirst().get().getContent()).isEqualTo(
                    returnComment.getContent());
            assertThat(responseDtoPage.get().findFirst().get().getCommenterNickname()).isEqualTo(
                    returnComment.getCommenter().getNickname());
        }
    }

    @Nested
    @DisplayName("댓글 생성")
    class createComment{
        @Test
        @DisplayName("댓글 생성 요청시 댓글이 생성된다.")
        void success_return_nothing() {
            // Given
            Long questionId = 1l;
            CommentCreateRequestDto requestDto = new CommentCreateRequestDto("content");
            Member member = getMockUserMember();
            MemberDto memberDto = MemberDto.from(member);
            Question question = getMockQuestion();

            given(questionRepository.getReferenceById(questionId)).willReturn(question);
            given(memberRepository.getReferenceById(memberDto.getId())).willReturn(member);

            // When
            commentService.createComment(questionId,requestDto, memberDto);

            // Then
            then(commentRepository).should().save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class updateComment{
        @Test
        @DisplayName("소유주가 댓글 수정 요청시 댓글이 수정된다.")
        void success_return_nothing() {
            // Given
            Long commentId = 1l;
            String modifiedContent = "ModifiedContent";
            CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto(modifiedContent);
            MemberDto memberDto = MemberDto.from(getMockUserMember());
            Comment comment = getMockComment();

            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // When
            commentService.updateComment(commentId, requestDto, memberDto);

            // Then
            assertThat(comment.getContent()).isEqualTo(modifiedContent);
        }

        @Test
        @DisplayName("소유주가 아닌 사람이 댓글 수정 요청시 예외가 반환된다.")
        void fail_return_exception() {
            // Given
            Long commentId = 1l;
            String modifiedContent = "ModifiedContent";
            CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto(modifiedContent);
            MemberDto anonymous = MemberDto.from(getMockAnonymousMember());
            Comment comment = getMockComment();

            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // When
            Throwable throwable =
                    catchThrowable(() -> commentService.updateComment(commentId, requestDto, anonymous));

            // Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());

        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class deleteComment{
        @Test
        @DisplayName("소유주가 댓글 삭제 요청시 댓글이 삭제된다.")
        void success_return_nothing() {
            // Given
            Long commentId = 1l;
            MemberDto memberDto = MemberDto.from(getMockUserMember());
            Comment comment = getMockComment();

            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // When
            commentService.deleteComment(commentId, memberDto);

            // Then
            then(commentRepository).should().delete(any(Comment.class));
        }

        @Test
        @DisplayName("소유주가 아닌 사람이 댓글 삭제 요청시 예외가 반환된다.")
        void fail_return_exception() {
            Long commentId = 1l;
            MemberDto anonymous = MemberDto.from(getMockAnonymousMember());
            Comment comment = getMockComment();

            given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

            // When
            Throwable throwable =
                    catchThrowable(() -> commentService.deleteComment(commentId,anonymous));

            // Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }
}