package org.finalproject.TMeRoom.lecture.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.finalproject.TMeRoom.common.util.MockProvider;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.dto.response.LectureDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.dto.response.StudentDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.lecture.service.StudentService;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.finalproject.TMeRoom.common.util.MockProvider.getMockManagerMember;
import static org.finalproject.TMeRoom.common.util.MockProvider.getMockStudentMember;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest(classes = {StudentService.class})
@Import(value = MockProvider.class)
@ActiveProfiles("test")
@DisplayName("학생 서비스")
class StudentServiceTest {
    private static final String MOCK_LECTURE_CODE = "code";
    private static final Pageable MOCK_PAGEABLE = PageRequest.of(0, 20);

    @Autowired
    private StudentService studentService;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private LectureRepository lectureRepository;


    public Student getMockStudent() {
        return Student.builder()
                .member(getMockStudentMember())
                .lecture(getMockLecture())
                .build();
    }

    public Lecture getMockLecture() {
        return Lecture.builder()
                .manager(getMockManagerMember())
                .lectureName("강의명")
                .lectureCode(MOCK_LECTURE_CODE)
                .build();
    }

    public MemberDto getMockStudentDto() {
        return MemberDto.builder()
                .id("student")
                .nickname("student")
                .build();
    }

    public MemberDto getMockManagerDto() {
        return MemberDto.builder()
                .id("manager")
                .nickname("manager")
                .build();
    }

    public MemberDto getMockAnonymousDto() {
        return MemberDto.builder()
                .id("anonymous")
                .nickname("anonymous")
                .build();
    }

    @Test
    @DisplayName("내가 수강중인 강의 목록 조회")
    void lookupMyLectures() {
        //Given
        MemberDto mockStudentDto = getMockStudentDto();
        Lecture lecture = getMockLecture();
        Student student = getMockStudent();
        Page<Student> mockPage = new PageImpl<>(List.of(student), MOCK_PAGEABLE, 0);

        given(memberRepository.getReferenceById("student")).willReturn(student.getMember());
        given(studentRepository.findAllByMember(eq(student.getMember()), any(Pageable.class))).willReturn(mockPage);

        //When
        Page<LectureDetailResponseDto> lectureResponsePage = studentService.lookupMyLectures(mockStudentDto, MOCK_PAGEABLE);

        //Then
        assertThat(lectureResponsePage.get().findFirst().get().getLectureCode()).isEqualTo(lecture.getLectureCode());
    }

    @Nested
    @DisplayName("수강신청")
    class aboutApplication {
        @Test
        @DisplayName("학생이 수강신청 요청시 수강 신청이 된다.")
        void GivenApplyLectureRequest_whenDismissApplicants_ThenApplyLecture() {
            //Given
            Member manager = getMockManagerMember();
            Member student = getMockStudentMember();
            MemberDto mockStudent = getMockStudentDto();

            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(
                    Optional.of(Lecture.builder().lectureCode(MOCK_LECTURE_CODE).lectureName("강의").manager(manager).build()));
            given(memberRepository.findById("student")).willReturn(Optional.of(student));

            //When
            studentService.applyLecture(MOCK_LECTURE_CODE, mockStudent);

            //Then
            then(studentRepository).should().save(any(Student.class));
        }

        @Test
        @DisplayName("학생이 수강신청 취소시 수강 신청이 취소 된다.")
        void GivenCancelApplicationRequest_whenDismissApplicants_ThenReturnException() {
            //Given
            Member mockStudent = getMockStudentMember();
            MemberDto mockStudentDto = getMockStudentDto();

            Lecture mockLecture = getMockLecture();
            Student studentEntity = Student.builder()
                    .member(mockStudent)
                    .lecture(mockLecture)
                    .build();


            given(studentRepository.findByMemberIdAndLectureCode(mockStudentDto.getId(), MOCK_LECTURE_CODE))
                    .willReturn(Optional.of(studentEntity));

            //When
            studentService.cancelApplication(MOCK_LECTURE_CODE, mockStudentDto);

            //Then
            then(studentRepository).should().delete(studentEntity);
        }
    }

    @Nested
    @DisplayName("수강신청 인원 목록 조회")
    class checkApplicants {
        @Test
        @DisplayName("관리자가 수강신청 인원 목록 조회 요청시 인원 목록을 반환한다")
        void GivenCheckApplicantsRequest_whenDismissApplicants_ThenReturnApplicantsList() {
            //Given
            MemberDto mockManagerDto = getMockManagerDto();
            Lecture lecture = getMockLecture();
            Student student = getMockStudent();
            Page<Student> mockPage = new PageImpl<>(List.of(student), MOCK_PAGEABLE, 0);


            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(Optional.of(lecture));
            given(studentRepository.findByLectureAndAcceptedAtNotNull(eq(lecture), any(Pageable.class))).willReturn(mockPage);

            //When
            Page<StudentDetailResponseDto> studentResponsePage =
                    studentService.lookupUnAcceptedApplicants(MOCK_LECTURE_CODE, mockManagerDto, MOCK_PAGEABLE);

            //Then
            assertThat(studentResponsePage.get().findFirst().get().getMemberNickname()).isEqualTo(
                    student.getMember().getNickname());
        }

        @Test
        @DisplayName("관리자가 아닌 사람이 수강신청 인원 목록 조회 요청시 예외를 발생시킨다")
        void GivenPermissionFail_whenCheckApplicants_ThenReturnException() {
            //Given
            MemberDto mockAnonymousDto = getMockAnonymousDto();
            Lecture lecture = getMockLecture();
            Student student = getMockStudent();
            Page<Student> mockPage = new PageImpl<>(List.of(student), MOCK_PAGEABLE, 0);


            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(Optional.of(lecture));
            given(studentRepository.findByLectureAndAcceptedAtNotNull(eq(lecture), any(Pageable.class))).willReturn(mockPage);

            //When
            Throwable throwable =
                    catchThrowable(() -> studentService.lookupUnAcceptedApplicants(MOCK_LECTURE_CODE, mockAnonymousDto, MOCK_PAGEABLE));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }


    @Nested
    @DisplayName("수강 신청 수락")
    class acceptApplicant {
        @Test
        @DisplayName("관리자가 수강신청 수락요청시 수락 된다.")
        void GivenAcceptApplicantRequest_whenDismissApplicants_ThenAcceptApplicant() {
            //Given
            Lecture lecture = getMockLecture();
            Student student = getMockStudent();
            MemberDto managerDto = getMockManagerDto();
            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(Optional.of(lecture));
            given(studentRepository.findByMemberIdAndLectureCode(student.getStudentId(), MOCK_LECTURE_CODE))
                    .willReturn(Optional.of(student));

            //When
            studentService.acceptApplicant(MOCK_LECTURE_CODE, student.getStudentId(), managerDto);

            //Then
            Duration duration = Duration.between(student.getAcceptedAt(), LocalDateTime.now());
            assertEquals(duration.getSeconds(), 0);
        }

        @Test
        @DisplayName("관리자가 아닌 사람이 수강신청 수락시 예외를 발생시킨다")
        void GivenPermissionFail_whenAcceptApplicants_ThenReturnException() {
            //Given
            Lecture lecture = getMockLecture();
            Student student = getMockStudent();
            MemberDto mockAnonymousDto = getMockAnonymousDto();
            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(Optional.of(lecture));
            given(studentRepository.findByMemberIdAndLectureCode(student.getStudentId(), MOCK_LECTURE_CODE))
                    .willReturn(Optional.of(student));

            //When
            Throwable throwable = catchThrowable(
                    () -> studentService.acceptApplicant(MOCK_LECTURE_CODE, student.getStudentId(), mockAnonymousDto));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }

    @Nested
    @DisplayName("수강 신청 반려")
    class rejectApplicant {
        @Test
        @DisplayName("관리자가 수강신청 반려 요청시 반려 시킨다.")
        void GivenRejectApplicantRequest_whenDismissApplicants_ThenRejectApplicant() {
            //Given
            Lecture lecture = getMockLecture();
            Student student = getMockStudent();
            MemberDto managerDto = getMockManagerDto();
            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(Optional.of(lecture));
            given(studentRepository.findByMemberIdAndLectureCode(student.getStudentId(), MOCK_LECTURE_CODE))
                    .willReturn(Optional.of(student));

            //When
            studentService.rejectApplicant(MOCK_LECTURE_CODE, student.getStudentId(), managerDto);

            //Then
            then(studentRepository).should().delete(student);
        }

        @Test
        @DisplayName("관리자가 아닌 사람이 수강신청 반려시 예외를 발생시킨다")
        void GivenPermissionFail_whenDismissApplicants_ThenReturnException() {
            //Given
            Lecture lecture = getMockLecture();
            Student student = getMockStudent();
            MemberDto mockAnonymousDto = getMockAnonymousDto();
            given(lectureRepository.findById(MOCK_LECTURE_CODE)).willReturn(Optional.of(lecture));
            given(studentRepository.findByMemberIdAndLectureCode(student.getStudentId(), MOCK_LECTURE_CODE))
                    .willReturn(Optional.of(student));

            //When
            Throwable throwable = catchThrowable(
                    () -> studentService.acceptApplicant(MOCK_LECTURE_CODE, student.getStudentId(), mockAnonymousDto));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }
}