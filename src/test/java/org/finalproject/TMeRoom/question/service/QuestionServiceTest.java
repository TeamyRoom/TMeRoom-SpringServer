package org.finalproject.TMeRoom.question.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.finalproject.TMeRoom.common.util.MockMemberProvider;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.finalproject.tmeroom.question.data.dto.request.QuestionCreateRequestDto;
import org.finalproject.tmeroom.question.data.dto.request.QuestionUpdateRequestDto;
import org.finalproject.tmeroom.question.data.dto.response.QuestionDetailResponseDto;
import org.finalproject.tmeroom.question.data.dto.response.QuestionListResponseDto;
import org.finalproject.tmeroom.question.data.entity.Question;
import org.finalproject.tmeroom.question.repository.QuestionRepository;
import org.finalproject.tmeroom.question.service.QuestionService;
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

@SpringBootTest(classes = {QuestionService.class})
@Import(value = MockMemberProvider.class)
@ActiveProfiles("test")
@DisplayName("질문 서비스")
class QuestionServiceTest {
    @Autowired
    private QuestionService questionService;
    @MockBean
    private QuestionRepository questionRepository;
    @MockBean
    private LectureRepository lectureRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private StudentRepository studentRepository;

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

    @Test
    @DisplayName("질문 생성 요청시 질문이 생성된다.")
    void GivenCreateQuestionRequest_whenCreateQuestion_ThenCreateQuestion() {
        // Given
        Lecture lecture = getMockLecture();
        Member member = getMockStudentMember();
        QuestionCreateRequestDto requestDto = new QuestionCreateRequestDto("title", "content", true);

        given(lectureRepository.findById(lecture.getLectureCode())).willReturn(Optional.of(lecture));
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        // When
        questionService.createQuestion(lecture.getLectureCode(), MemberDto.from(member), requestDto);

        // Then
        then(questionRepository).should().save(any(Question.class));
    }

    @Nested
    @DisplayName("질문 목록 조회")
    class lookupQuestions {
        @Test
        @DisplayName("관리자가 질문 목록 조회 요청시 전체 질문 목록을 반환한다.")
        void GivenManagerLookupQuestionsRequest_whenLookupQuestions_ThenQuestionListReturn() {
            // Given
            String lectureCode = "1234";
            Pageable pageable = PageRequest.of(0, 20);
            Member reader = getMockManagerMember();
            Lecture lecture = getMockLecture();
            Question returnQuestion = getMockQuestion();
            Page<Question> questionPage = new PageImpl<>(List.of(returnQuestion), pageable, 0);

            given(lectureRepository.getReferenceById(lectureCode)).willReturn(lecture);
            given(questionRepository.findByLecture(lecture, pageable)).willReturn(questionPage);

            // When
            Page<QuestionListResponseDto> responseDtoPage = questionService.lookupAllQuestions(lectureCode, pageable,
                    MemberDto.from(reader));

            // Then
            assertThat(responseDtoPage.get().findFirst().get().getQuestionId()).isEqualTo(
                    returnQuestion.getId());
        }

        @Test
        @DisplayName("강사가 질문 목록 조회 요청시 전체 질문 목록을 반환한다.")
        void GivenTeacherLookupQuestionsRequest_whenLookupQuestions_ThenQuestionListReturn() {
            // Given
            String lectureCode = "1234";
            Pageable pageable = PageRequest.of(0, 20);
            Member reader = getMockTeacherMember();
            Lecture lecture = getMockLecture();
            Teacher teacher = Teacher.builder().member(reader).lecture(lecture).build();
            Question returnQuestion = getMockQuestion();
            Page<Question> questionPage = new PageImpl<>(List.of(returnQuestion), pageable, 0);

            given(lectureRepository.getReferenceById(lectureCode)).willReturn(lecture);
            given(questionRepository.findByLecture(lecture, pageable)).willReturn(questionPage);
            given(teacherRepository.findByMemberIdAndLectureCode(reader.getId(), lectureCode)).willReturn(
                    Optional.of(teacher));

            // When
            Page<QuestionListResponseDto> responseDtoPage = questionService.lookupAllQuestions(lectureCode, pageable,
                    MemberDto.from(reader));

            // Then
            assertThat(responseDtoPage.get().findFirst().get().getQuestionId()).isEqualTo(
                    returnQuestion.getId());
        }

        @Test
        @DisplayName("권한이 없는 사용자가 전체 질문 목록 조회 요청시 예외를 반환한다.")
        void GivenPermissionFailRequest_whenLookupAllQuestions_ThenExceptionReturn() {
            // Given
            String lectureCode = "1234";
            Pageable pageable = PageRequest.of(0, 20);
            Member reader = getMockAnonymousMember();
            Lecture lecture = getMockLecture();

            given(lectureRepository.getReferenceById(lectureCode)).willReturn(lecture);

            // When
            Throwable throwable =
                    catchThrowable(() -> questionService.lookupAllQuestions(lectureCode, pageable,
                            MemberDto.from(reader)));

            // Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_ACCESS_PERMISSION.getMessage());
        }

        @Test
        @DisplayName("학생이 질문 목록 조회 요청시 공개 질문 목록을 반환한다.")
        void GivenStudentLookupQuestionsRequest_whenLookupQuestions_ThenQuestionListReturn() {
            // Given
            String lectureCode = "1234";
            Pageable pageable = PageRequest.of(0, 20);
            Member reader = getMockStudentMember();
            Lecture lecture = getMockLecture();
            Student student = Student.builder().member(reader).lecture(lecture).build();
            Question returnQuestion = getMockQuestion();
            Page<Question> questionPage = new PageImpl<>(List.of(returnQuestion), pageable, 0);

            given(lectureRepository.getReferenceById(lectureCode)).willReturn(lecture);
            given(questionRepository.findByLectureAndAuthorOrIsPublic(lecture, student.getMember(), true,
                    pageable)).willReturn(questionPage);
            given(studentRepository.findByMemberIdAndLectureCode(reader.getId(), lectureCode)).willReturn(
                    Optional.of(student));

            // When
            Page<QuestionListResponseDto> responseDtoPage = questionService.lookupPublicQuestions(lectureCode, pageable,
                    MemberDto.from(reader));

            // Then
            assertThat(responseDtoPage.get().findFirst().get().getQuestionId()).isEqualTo(
                    returnQuestion.getId());
        }

        @Test
        @DisplayName("강의에 없는 사용자가 질문 목록 조회 요청시 예외를 반환한다.")
        void GivenPermissionFailRequest_whenLookupQuestions_ThenExceptionReturn() {
            // Given
            String lectureCode = "1234";
            Pageable pageable = PageRequest.of(0, 20);
            Member reader = getMockAnonymousMember();
            Lecture lecture = getMockLecture();

            given(lectureRepository.getReferenceById(lectureCode)).willReturn(lecture);

            // When
            Throwable throwable =
                    catchThrowable(() -> questionService.lookupPublicQuestions(lectureCode, pageable,
                            MemberDto.from(reader)));

            // Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_STUDENT_ID.getMessage());
        }
    }

    @Nested
    @DisplayName("질문 단일 조회")
    class readQuestion {
        @Test
        @DisplayName("공개 질문 상세 내용 요청시 상세 내용을 반환한다.")
        void GivenReadPublicQuestionRequest_whenReadQuestion_ThenQuestionDetailReturn() {
            // Given
            String lectureCode = "1234";
            Long questionId = 1l;
            Member reader = getMockStudentMember();
            Question question = getMockQuestion();
            question.makePublic();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            // When
            QuestionDetailResponseDto responseDto =
                    questionService.readQuestion(lectureCode, questionId, MemberDto.from(reader));

            // Then
            assertThat(responseDto.getQuestionId()).isEqualTo(question.getId());
            assertThat(responseDto.getQuestionContent()).isEqualTo(question.getContent());
            assertThat(responseDto.getQuestionTitle()).isEqualTo(question.getTitle());
            assertThat(responseDto.getAuthorNickname()).isEqualTo(question.getAuthorNickname());
        }

        @Test
        @DisplayName("작성자가 비공개 질문 상세 내용 요청시 상세 내용을 반환한다.")
        void GivenAuthorReadPrivateQuestionRequest_whenReadQuestion_ThenQuestionDetailReturn() {
            // Given
            String lectureCode = "1234";
            Long questionId = 1l;
            Member author = getMockStudentMember();
            Question question = getMockQuestion();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            // When
            QuestionDetailResponseDto responseDto =
                    questionService.readQuestion(lectureCode, questionId, MemberDto.from(author));

            // Then
            assertThat(responseDto.getQuestionId()).isEqualTo(questionId);
        }

        @Test
        @DisplayName("강사가 비공개 질문 상세 내용 요청시 상세 내용을 반환한다.")
        void GivenTeacherReadPrivateQuestionRequest_whenReadQuestion_ThenQuestionDetailReturn() {
            // Given
            String lectureCode = "1234";
            Long questionId = 1l;
            Member teacherMember = getMockTeacherMember();
            Teacher teacher = Teacher.builder()
                    .lecture(getMockLecture())
                    .member(teacherMember)
                    .build();
            Question question = getMockQuestion();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));
            given(teacherRepository.findByMemberIdAndLectureCode(teacherMember.getId(), lectureCode)).willReturn(
                    Optional.of(teacher));

            // When
            QuestionDetailResponseDto responseDto =
                    questionService.readQuestion(lectureCode, questionId, MemberDto.from(teacherMember));

            // Then
            assertThat(responseDto.getQuestionId()).isEqualTo(questionId);
        }

        @Test
        @DisplayName("관리자가 비공개 질문 상세 내용 요청시 상세 내용을 반환한다.")
        void GivenManagerReadPrivateQuestionRequest_whenReadQuestion_ThenQuestionDetailReturn() {
            // Given
            String lectureCode = "1234";
            Long questionId = 1l;
            Member manager = getMockManagerMember();
            Question question = getMockQuestion();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            // When
            QuestionDetailResponseDto responseDto =
                    questionService.readQuestion(lectureCode, questionId, MemberDto.from(manager));

            // Then
            assertThat(responseDto.getQuestionId()).isEqualTo(questionId);
        }

        @Test
        @DisplayName("권한이 없는 사람이 비공개 질문 상세 내용 요청시 예외를 반환한다.")
        void GivenFailPermissionRequest_whenReadQuestion_ThenExceptionReturn() {
            // Given
            String lectureCode = "1234";
            Long questionId = 1l;
            Member reader = getMockAnonymousMember();
            Question question = getMockQuestion();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            // When
            Throwable throwable =
                    catchThrowable(() -> questionService.readQuestion(lectureCode, questionId, MemberDto.from(reader)));

            // Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_READ_QUESTION_PERMISSION.getMessage());
        }
    }

    @Nested
    @DisplayName("질문 수정")
    class updateQuestion {
        @Test
        @DisplayName("소유자가 질문 수정 요청시 질문이 수정된다.")
        void GivenUpdateQuestionRequest_whenUpdateQuestion_ThenUpdateQuestion() {
            // Given
            Long questionId = 1l;
            Member author = getMockStudentMember();
            Question question = getMockQuestion();
            QuestionUpdateRequestDto requestDto =
                    new QuestionUpdateRequestDto(question.getTitle(), question.getContent(), true);
            String modifiedTitle = "modifiedTitle";
            String modifiedContent = "modifiedContent";
            requestDto.setTitle(modifiedTitle);
            requestDto.setContent(modifiedContent);

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            // When
            questionService.updateQuestion(questionId, MemberDto.from(author), requestDto);

            // Then
            assertThat(question.getTitle()).isEqualTo(modifiedTitle);
            assertThat(question.getContent()).isEqualTo(modifiedContent);
        }

        @Test
        @DisplayName("소유자가 아닌 사람이 질문 수정 요청시 예외 반환.")
        void GivenPermissionFailRequest_whenUpdateQuestion_ThenExceptionReturn() {
            // Given
            Long questionId = 1l;
            Member author = getMockAnonymousMember();
            Question question = getMockQuestion();
            QuestionUpdateRequestDto requestDto =
                    new QuestionUpdateRequestDto(question.getTitle(), question.getContent(), true);

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            //When
            Throwable throwable = catchThrowable(
                    () -> questionService.updateQuestion(questionId, MemberDto.from(author), requestDto));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }


    @Nested
    @DisplayName("질문 삭제")
    class deleteQuestion {
        @Test
        @DisplayName("소유자가 삭제 요청시 질문이 삭제된다.")
        void GivenDeleteQuestionRequest_whenDeleteQuestion_ThenDeleteQuestion() {
            // Given
            Long questionId = 1l;
            Member author = getMockStudentMember();
            Question question = getMockQuestion();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            //When
            questionService.deleteQuestion(questionId, MemberDto.from(author));

            //Then
            then(questionRepository).should().delete(question);
        }

        @Test
        @DisplayName("소유자가 아닌 사람이 삭제 요청시 예외 반환.")
        void GivenPermissionFailRequest_whenDeleteQuestion_ThenExceptionReturn() {
            // Given
            Long questionId = 1l;
            Member author = getMockAnonymousMember();
            Question question = getMockQuestion();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            //When
            Throwable throwable =
                    catchThrowable(() -> questionService.deleteQuestion(questionId, MemberDto.from(author)));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }


    @Nested
    @DisplayName("질문 전체 공개")
    class openQuestion {
        @Test
        @DisplayName("소유자가 공개 요청시 질문이 공개된다.")
        void GivenOpenQuestionRequest_whenOpenQuestion_ThenOpenQuestion() {
            // Given
            Long questionId = 1l;
            Member author = getMockStudentMember();
            Question question = getMockQuestion();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            //When
            questionService.openQuestion(questionId, MemberDto.from(author));

            //Then
            assertThat(question.getIsPublic()).isEqualTo(true);
        }

        @Test
        @DisplayName("소유자가 아닌 사람이 공개 요청시 예외 반환.")
        void GivenPermissionFailRequest_whenOpenQuestion_ThenExceptionReturn() {
            // Given
            Long questionId = 1l;
            Member author = getMockAnonymousMember();
            Question question = getMockQuestion();

            given(questionRepository.findById(questionId)).willReturn(Optional.of(question));

            //When
            Throwable throwable =
                    catchThrowable(() -> questionService.openQuestion(questionId, MemberDto.from(author)));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }
}