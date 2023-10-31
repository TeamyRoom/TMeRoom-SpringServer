package org.finalproject.tmeroom.lecture.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LecturePermissionChecker {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public boolean isManager(Lecture lecture, MemberDto memberDto) {
        if (memberDto == null || !lecture.getManager().isIdMatch(memberDto.getId())) {
            throw new ApplicationException(ErrorCode.INVALID_PERMISSION);
        }
        return true;
    }

    public boolean isAcceptedTeacher(MemberDto memberDto, String lectureCode) {
        return teacherRepository.findByMemberIdAndLectureCode(memberDto.getId(), lectureCode)
                .filter(Teacher::isAccepted)
                .isPresent();
    }

    public boolean isAcceptedStudent(MemberDto memberDto, String lectureCode) {
        return studentRepository.findByMemberIdAndLectureCode(memberDto.getId(), lectureCode)
                .filter(Student::isAccepted)
                .isPresent();
    }

    public boolean isStudentWaiting(MemberDto memberDto, String lectureCode) {
        Optional<Student> foundStudent = studentRepository.findByMemberIdAndLectureCode(memberDto.getId(), lectureCode);
        return foundStudent.isPresent() && !foundStudent.get().isAccepted();
    }
}
