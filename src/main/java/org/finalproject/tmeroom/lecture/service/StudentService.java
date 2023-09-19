package org.finalproject.tmeroom.lecture.service;

import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@EnableWebSecurity
public class StudentService extends LectureCommon {
    private final StudentRepository studentRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    // 내 강의 목록 보기
    public Page<LectureDetailResponseDto> lookupMyLectures(MemberDto memberDTO, Pageable pageable) {
        Member member = memberRepository.getReferenceById(memberDTO.getId());

        Page<Student> myLectures = studentRepository.findByMember(pageable, member);
        return myLectures.map(LectureDetailResponseDto::from);
    }

    //수강 신청
    public void applyLecture(String lectureCode, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode).orElseThrow();

        checkPermission(lecture, memberDTO);

        Member registeringStudent = memberRepository.findById(memberDTO.getId()).orElseThrow();

        Student student = Student.builder()
                .lecture(lecture)
                .student(registeringStudent)
                .appliedAt(LocalDateTime.now())
                .build();

        studentRepository.save(student);
    }

    //수강 신청 철회
    public void cancelApplication(String lectureCode, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode).orElseThrow();

        Student student = studentRepository.findByMemberIdAndLectureCode(memberDTO.getId(), lectureCode);

        studentRepository.delete(student);
    }

    //강의 수강 신청 인원 목록 조회
    public Page<StudentDetailResponseDto> checkApplicants(String lectureCode, MemberDto memberDTO, Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode).orElseThrow();
        checkPermission(lecture, memberDTO);

        Page<Student> applicants = studentRepository.findByLecture(pageable, lecture);
        return applicants.map(StudentDetailResponseDto::from);
    }

    //수강 신청 수락
    public void acceptApplicant(String lectureCode, String applicantId, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode).orElseThrow();
        checkPermission(lecture, memberDTO);

        Student student = studentRepository.findByMemberIdAndLectureCode(applicantId, lectureCode);

        student.acceptStudent();
    }

    //수강 신청 반려
    public void rejectApplicant(String lectureCode, String applicantId, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode).orElseThrow();
        checkPermission(lecture, memberDTO);

        Student student = studentRepository.findByMemberIdAndLectureCode(applicantId, lectureCode);

        studentRepository.delete(student);
    }
}
