package org.finalproject.TMeRoom.lecture.service;

import org.finalproject.tmeroom.lecture.data.dto.response.StudentDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.lecture.service.StudentService;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest(classes = {StudentService.class})
@ActiveProfiles("test")
@DisplayName("학생 서비스")
class StudentServiceTest {
    @Autowired
    private StudentService studentService;
    @MockBean
    private StudentRepository studentRepository;
    @MockBean
    private MemberRepository memberRepository;
    @MockBean
    private LectureRepository lectureRepository;

    public Member getMockManager() {
        return Member.builder()
                .id("manager")
                .pw("encodedPw")
                .email("testGuest@test.com")
                .nickname("manager")
                .role(MemberRole.USER)
                .build();
    }

    public Member getMockStudentMember() {
        return Member.builder()
                .id("student")
                .pw("encodedPw")
                .email("testGuest@test.com")
                .nickname("student")
                .role(MemberRole.USER)
                .build();
    }


    public Student getMockStudent(){
        return Student.builder()
                .member(getMockStudentMember())
                .lecture(getMockLecture())
                .appliedAt(LocalDateTime.now())
                .build();
    }

    public Lecture getMockLecture() {
        return Lecture.builder()
                .manager(getMockManager())
                .lectureName("강의명")
                .lectureCode("code")
                .build();
    }

    public MemberDto getMockStudentDto(){
        return MemberDto.builder()
                .id("student")
                .nickname("student")
                .build();
    }

    public MemberDto getMockManagerDto(){
        return MemberDto.builder()
                .id("manager")
                .nickname("manager")
                .build();
    }

    @Test
    void lookupMyLectures() {
        //TODO : 수강 목록 조회 테스트
    }

    @Test
    @DisplayName("수강신청")
    void applyLecture() {
        //Given
        Member manager = getMockManager();
        MemberDto mockStudent = getMockStudentDto();

        String lectureCode = "code";

        given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(Lecture.builder().lectureCode(lectureCode).lectureName("강의").manager(manager).build()));
        given(memberRepository.findById("student")).willReturn(Optional.of(Member.builder().id("student").build()));

        //When
        studentService.applyLecture(lectureCode, mockStudent);

        //Then
        then(studentRepository).should().save(any(Student.class));
    }

    @Test
    @DisplayName("수강신청 취소")
    void cancelApplication() {
        //Given
        String lectureCode = "code";
        Member mockStudent = getMockStudentMember();
        MemberDto mockStudentDto = getMockStudentDto();

        Lecture mockLecture = getMockLecture();
        Student studentEntity = Student.builder()
                .member(mockStudent)
                .lecture(mockLecture)
                .appliedAt(LocalDateTime.now())
                .build();


        given(studentRepository.findByMemberIdAndLectureCode(mockStudentDto.getId(), lectureCode)).willReturn(studentEntity);

        //When
        studentService.cancelApplication(lectureCode, mockStudentDto);

        //Then
        then(studentRepository).should().delete(studentEntity);
    }


    @Test
    @DisplayName("수강신청 인원 목록 조회")
    void checkApplicants() {
        //Given
        String lectureCode = "code";
        MemberDto mockManagerDto = getMockManagerDto();
        Lecture lecture = getMockLecture();
        Student student = getMockStudent();
        Pageable pageable = PageRequest.of(0, 20);
        Page<Student> studentPage = new PageImpl<>(List.of(student), pageable, 0);


        given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
        given(studentRepository.findByLecture(any(Pageable.class), eq(lecture))).willReturn(studentPage);

        //When
        Page<StudentDetailResponseDto> studentResponsePage = studentService.checkApplicants(lectureCode, mockManagerDto, pageable);

        //Then
        assertThat(studentResponsePage.get().findFirst().get().getMemberNickname()).isEqualTo(student.getMember().getNickname());
    }

    @Test
    @DisplayName("수강 신청 수락")
    void acceptApplicant() {
        //Given
        Lecture lecture = getMockLecture();
        Student student = getMockStudent();
        String lectureCode = "code";
        MemberDto managerDto = getMockManagerDto();
        given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
        given(studentRepository.findByMemberIdAndLectureCode(student.getStudentId(),lectureCode)).willReturn(student);

        //When
        studentService.acceptApplicant(lectureCode, student.getStudentId(), managerDto);

        //Then
        Duration duration = Duration.between(student.getAcceptedAt(), LocalDateTime.now());
        assertEquals(duration.getSeconds(), 0);
    }

    @Test
    @DisplayName("수강 신청 반려")
    void rejectApplicant() {
        //Given
        Lecture lecture = getMockLecture();
        Student student = getMockStudent();
        String lectureCode = "code";
        MemberDto managerDto = getMockManagerDto();
        given(lectureRepository.findById(lectureCode)).willReturn(Optional.of(lecture));
        given(studentRepository.findByMemberIdAndLectureCode(student.getStudentId(),lectureCode)).willReturn(student);

        //When
        studentService.rejectApplicant(lectureCode, student.getStudentId(), managerDto);

        //Then
        then(studentRepository).should().delete(student);
    }
}