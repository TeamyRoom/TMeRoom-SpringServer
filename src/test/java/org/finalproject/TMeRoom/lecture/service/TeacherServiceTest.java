package org.finalproject.TMeRoom.lecture.service;

import org.assertj.core.api.AssertionsForClassTypes;
import org.finalproject.TMeRoom.common.util.MockMemberProvider;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.common.service.MailService;
import org.finalproject.tmeroom.lecture.data.dto.request.AppointTeacherRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.response.TeacherDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.lecture.service.TeacherService;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.finalproject.TMeRoom.common.util.MockMemberProvider.getMockManagerMember;
import static org.finalproject.TMeRoom.common.util.MockMemberProvider.getMockTeacherMember;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest(classes = {TeacherService.class})
@Import(value = MockMemberProvider.class)
@ActiveProfiles("test")
@DisplayName("강사 서비스")
class TeacherServiceTest {
    @Autowired
    private TeacherService teacherService;
    @MockBean
    private TeacherRepository teacherRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private LectureRepository lectureRepository;
    @MockBean
    private MailService mailService;

    private Lecture getMockLecture() {
        return Lecture.builder()
                .manager(getMockManagerMember())
                .lectureName("강의명")
                .lectureCode("code")
                .build();
    }

    private MemberDto getMockManagerDto() {
        return MemberDto.builder()
                .id("manager")
                .nickname("manager")
                .build();
    }

    private Teacher getMockTeacher() {
        return Teacher.builder()
                .lecture(getMockLecture())
                .member(getMockTeacherMember())
                .build();
    }

    public MemberDto getMockAnonymousDto() {
        return MemberDto.builder()
                .id("anonymous")
                .nickname("anonymous")
                .build();
    }

    @Nested
    @DisplayName("강사 목록 조회")
    class lookupTeachers {
        @Test
        @DisplayName("관리자강사 목록 조회 요청시 강사 목록을 반환한다.")
        void GivenLookupTeachersRequest_whenDismissApplicants_ThenReturnTeacherList() {
            //Given
            String lectureCode = "code";
            MemberDto mockManagerDto = getMockManagerDto();
            Lecture lecture = getMockLecture();
            Teacher teacher = getMockTeacher();
            Pageable pageable = PageRequest.of(0, 20);
            Page<Teacher> studentPage = new PageImpl<>(List.of(teacher), pageable, 0);


            given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
            given(teacherRepository.findByLecture(eq(lecture), any(Pageable.class))).willReturn(studentPage);

            //When
            Page<TeacherDetailResponseDto> teagerResponsePage =
                    teacherService.lookupTeachers(lectureCode, mockManagerDto, pageable);

            //Then
            assertThat(teagerResponsePage.get().findFirst().get().getNickName()).isEqualTo(
                    teacher.getMember().getNickname());
        }

        @Test
        @DisplayName("관리자가 아닌 사람이 강사 목록 조회 요청시 예외를 발생시킨다")
        void GivenPermissionFail_whenLookupTeachers_ThenReturnException() {
            //Given
            String lectureCode = "code";
            MemberDto mockAnonymousDto = getMockAnonymousDto();
            Lecture lecture = getMockLecture();
            Teacher teacher = getMockTeacher();
            Pageable pageable = PageRequest.of(0, 20);
            Page<Teacher> studentPage = new PageImpl<>(List.of(teacher), pageable, 0);


            given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
            given(teacherRepository.findByLecture(eq(lecture), any(Pageable.class))).willReturn(studentPage);

            //When
            Throwable throwable =
                    catchThrowable(() -> teacherService.lookupTeachers(lectureCode, mockAnonymousDto, pageable));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }

    @Nested
    @DisplayName("강사 임명")
    class appointTeacher {
        @Test
        @DisplayName("관리자가 강사 임명시 강사가 임명된다.")
        void GivenAppointTeacherRequest_whenDismissApplicants_ThenAppointTeacher() {
            //Given
            Lecture lecture = getMockLecture();
            String lectureCode = "code";
            MemberDto mockManagerDto = getMockManagerDto();
            Member teacher = getMockTeacherMember();
            AppointTeacherRequestDto dto = new AppointTeacherRequestDto();
            dto.setTeacherId("teacher");

            given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
            given(memberRepository.findById(dto.getTeacherId())).willReturn(Optional.of(teacher));

            //When
            teacherService.appointTeacher(lectureCode, mockManagerDto, dto);

            //Then
            then(teacherRepository).should().save(any(Teacher.class));
        }

        @Test
        @DisplayName("관리자가 아닌 사람이 강사 임명시 예외를 발생시킨다")
        void GivenPermissionFail_whenAppointTeacher_ThenReturnException() {
            //Given
            Lecture lecture = getMockLecture();
            String lectureCode = "code";
            MemberDto mockAnonymousDto = getMockAnonymousDto();
            Member teacher = getMockTeacherMember();
            AppointTeacherRequestDto dto = new AppointTeacherRequestDto();
            dto.setTeacherId("teacher");

            given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
            given(memberRepository.findById(dto.getTeacherId())).willReturn(Optional.of(teacher));

            //When
            Throwable throwable =
                    catchThrowable(() -> teacherService.appointTeacher(lectureCode, mockAnonymousDto, dto));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }

    @Nested
    @DisplayName("강사 해임")
    class dismissTeacher {
        @Test
        @DisplayName("관리자가 아닌 사람이 강사 해임시 강사가 해임 된다.")
        void GivenDismissTeacherRequest_whenDismissApplicants_ThenDismissTeacher() {
            //Given
            Lecture lecture = getMockLecture();
            String lectureCode = "code";
            MemberDto mockManagerDto = getMockManagerDto();
            Teacher teacher = getMockTeacher();

            given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
            given(teacherRepository.findByMemberIdAndLectureCode(teacher.getTeacherId(), lectureCode))
                    .willReturn(Optional.of(teacher));

            //When
            teacherService.dismissTeacher(lectureCode, teacher.getTeacherId(), mockManagerDto);

            //Then
            then(teacherRepository).should().delete(teacher);
        }

        @Test
        @DisplayName("관리자가 아닌 사람이 강사 해임시 예외를 발생시킨다")
        void GivenPermissionFail_whenDismissTeacher_ThenReturnException() {
            //Given
            Lecture lecture = getMockLecture();
            String lectureCode = "code";
            MemberDto mockAnonymousDto = getMockAnonymousDto();
            Teacher teacher = getMockTeacher();

            given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
            given(teacherRepository.findByMemberIdAndLectureCode(teacher.getTeacherId(), lectureCode))
                    .willReturn(Optional.of(teacher));

            //When
            Throwable throwable = catchThrowable(
                    () -> teacherService.dismissTeacher(lectureCode, teacher.getTeacherId(), mockAnonymousDto));

            //Then
            AssertionsForClassTypes.assertThat(throwable)
                    .isInstanceOf(ApplicationException.class)
                    .hasMessage(ErrorCode.INVALID_PERMISSION.getMessage());
        }
    }
}