package org.finalproject.tmeroom.lecture.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.dto.request.AppointTeacherRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.response.TeacherDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
@EnableWebSecurity
public class TeacherService extends LectureCommon {
    private final TeacherRepository teacherRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    //강의 강사 조회
    public Page<TeacherDetailResponseDto> lookupTeachers(String lectureCode, MemberDto memberDto, Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        checkPermission(lecture, memberDto);

        Page<Teacher> teachers = teacherRepository.findByLecture(pageable, lecture);

        return teachers.map(TeacherDetailResponseDto::from);
    }

    //강의 강사 임명
    //TODO: 강사 임명시 바로 임명되는 로직에서 -> 이메일 수락 후 임명되는 로직으로 변경 필요
    public void appointTeacher(String lectureCode, MemberDto memberDTO, AppointTeacherRequestDto requestDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        checkPermission(lecture, memberDTO);

        Member appointedMember = memberRepository.findById(requestDTO.getTeacherId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = Teacher.builder()
                .lecture(lecture)
                .member(appointedMember)
                .build();

        teacherRepository.save(teacher);
    }

    //강의 강사 해임
    public void dismissTeacher(String lectureCode, String teacherId, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        checkPermission(lecture, memberDTO);

        Teacher dismissedTeacher = teacherRepository.findByMemberIdAndLectureCode(teacherId, lectureCode);

        teacherRepository.delete(dismissedTeacher);
    }
}
