package org.finalproject.tmeroom.lecture.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.dto.response.LectureDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.dto.response.StudentDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@EnableWebSecurity
public class StudentService{
    private final LecturePermissionChecker lecturePermissionChecker;
    private final StudentRepository studentRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    // 내 강의 목록 보기
    public Page<LectureDetailResponseDto> lookupMyLectures(MemberDto memberDto, Pageable pageable) {
        Member member = memberRepository.getReferenceById(memberDto.getId());

        Page<Student> myLectures = studentRepository.findAllByMember(member, pageable);
        return myLectures.map(LectureDetailResponseDto::fromStudent);
    }

    //수강 신청
    @Transactional
    public void applyLecture(String lectureCode, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        Member registeringStudent = memberRepository.findById(memberDTO.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Student student = Student.builder()
                .lecture(lecture)
                .member(registeringStudent)
                .build();

        studentRepository.save(student);
    }

    //수강 신청 철회
    @Transactional
    public void cancelApplication(String lectureCode, MemberDto memberDTO) {
        Student student = studentRepository.findByMemberIdAndLectureCode(memberDTO.getId(), lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_STUDENT_ID));

        studentRepository.delete(student);
    }

    //전체 학생 조회
    public Page<StudentDetailResponseDto> lookupApplicants(String lectureCode, MemberDto memberDto,
                                                                     Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        checkPermission(lecture, lectureCode, memberDto);

        Page<Student> applicants = studentRepository.findByLecture(lecture, pageable);
        return applicants.map(StudentDetailResponseDto::from);
    }

    //수강 신청 인원 목록 조회
    public Page<StudentDetailResponseDto> lookupUnAcceptedApplicants(String lectureCode, MemberDto memberDto,
                                                                     Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        checkPermission(lecture, lectureCode, memberDto);

        Page<Student> applicants = studentRepository.findByLectureAndAcceptedAtIsNull(lecture, pageable);
        return applicants.map(StudentDetailResponseDto::from);
    }

    //수강중인 인원 목록 조회
    public Page<StudentDetailResponseDto> lookupAcceptedApplicants(String lectureCode, MemberDto memberDto,
                                                                   Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        checkPermission(lecture, lectureCode, memberDto);

        Page<Student> applicants = studentRepository.findByLectureAndAcceptedAtNotNull(lecture, pageable);
        return applicants.map(StudentDetailResponseDto::from);
    }

    //수강 신청 수락
    @Transactional
    public void acceptApplicant(String lectureCode, String applicantId, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        checkPermission(lecture, lectureCode, memberDTO);

        Student student = studentRepository.findByMemberIdAndLectureCode(applicantId, lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_STUDENT_ID));

        student.acceptStudent();
    }

    //수강 신청 반려
    @Transactional
    public void rejectApplicant(String lectureCode, String applicantId, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        checkPermission(lecture, lectureCode, memberDTO);

        Student student = studentRepository.findByMemberIdAndLectureCode(applicantId, lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_STUDENT_ID));

        studentRepository.delete(student);
    }
    
    private void checkPermission(Lecture lecture, String lectureCode, MemberDto memberDto){
        if(!lecturePermissionChecker.isAcceptedTeacher(memberDto, lectureCode)){
            lecturePermissionChecker.isManager(lecture, memberDto);
        }
    }
}
