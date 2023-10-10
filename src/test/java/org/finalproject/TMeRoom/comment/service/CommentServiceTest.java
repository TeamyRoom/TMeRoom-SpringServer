package org.finalproject.TMeRoom.comment.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.finalproject.TMeRoom.common.util.MockProvider;
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
import static org.finalproject.TMeRoom.common.util.MockProvider.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest(classes = {CommentService.class})
@Import(value = MockProvider.class)
@ActiveProfiles("test")
@DisplayName("댓글 서비스")
class CommentServiceTest {
    private static final Long MOCK_QUESTION_ID = 1L;
    private static final Long MOCK_COMMENT_ID = 1L;
    private static final Pageable MOCK_PAGEABLE = PageRequest.of(0, 20);

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
                .id(MOCK_QUESTION_ID)
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
                .id(MOCK_COMMENT_ID)
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
            Question question = getMockQuestion();
            Comment returnComment = getMockComment();
            Page<Comment> questionPage = new PageImpl<>(List.of(returnComment), MOCK_PAGEABLE, 0);

            given(questionRepository.getReferenceById(MOCK_QUESTION_ID)).willReturn(question);
            given(commentRepository.findByQuestion(question, MOCK_PAGEABLE)).willReturn(questionPage);

            // When
            Page<CommentDetailResponseDto> responseDtoPage = commentService.readComments(MOCK_QUESTION_ID, MOCK_PAGEABLE);

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
            CommentCreateRequestDto requestDto = new CommentCreateRequestDto("content");
            Member member = getMockUserMember();
            MemberDto memberDto = MemberDto.from(member);
            Question question = getMockQuestion();

            given(questionRepository.getReferenceById(MOCK_QUESTION_ID)).willReturn(question);
            given(memberRepository.getReferenceById(memberDto.getId())).willReturn(member);

            // When
            commentService.createComment(MOCK_QUESTION_ID,requestDto, memberDto);

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
            String modifiedContent = "ModifiedContent";
            CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto(modifiedContent);
            MemberDto memberDto = MemberDto.from(getMockUserMember());
            Comment comment = getMockComment();

            given(commentRepository.findById(MOCK_COMMENT_ID)).willReturn(Optional.of(comment));

            // When
            commentService.updateComment(MOCK_COMMENT_ID, requestDto, memberDto);

            // Then
            assertThat(comment.getContent()).isEqualTo(modifiedContent);
        }

        @Test
        @DisplayName("소유주가 아닌 사람이 댓글 수정 요청시 예외가 반환된다.")
        void fail_return_exception() {
            // Given
            String modifiedContent = "ModifiedContent";
            CommentUpdateRequestDto requestDto = new CommentUpdateRequestDto(modifiedContent);
            MemberDto anonymous = MemberDto.from(getMockAnonymousMember());
            Comment comment = getMockComment();

            given(commentRepository.findById(MOCK_COMMENT_ID)).willReturn(Optional.of(comment));

            // When
            Throwable throwable =
                    catchThrowable(() -> commentService.updateComment(MOCK_COMMENT_ID, requestDto, anonymous));

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
            MemberDto memberDto = MemberDto.from(getMockUserMember());
            Comment comment = getMockComment();

            given(commentRepository.findById(MOCK_COMMENT_ID)).willReturn(Optional.of(comment));

            // When
            commentService.deleteComment(MOCK_COMMENT_ID, memberDto);

            // Then
            then(commentRepository).should().delete(any(Comment.class));
        }

        @Test
        @DisplayName("소유주가 아닌 사람이 댓글 삭제 요청시 예외가 반환된다.")
        void fail_return_exception() {
            MemberDto anonymous = MemberDto.from(getMockAnonymousMember());
            Comment comment = getMockComment();

            given(commentRepository.findById(MOCK_COMMENT_ID)).willReturn(Optional.of(comment));

            // When
            Throwable throwable =
                    catchThrowable(() -> commentService.deleteComment(MOCK_COMMENT_ID,anonymous));

            // Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }
}