package org.finalproject.tmeroom.lecture.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.common.service.MailService;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
@EnableWebSecurity
public class TeacherService extends LectureCommon {
    private final TeacherRepository teacherRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;
    private final MailService mailService;

    //강의 강사 조회
    public Page<TeacherDetailResponseDto> lookupTeachers(String lectureCode, MemberDto memberDto, Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        checkPermission(lecture, memberDto);

        Page<Teacher> teachers = teacherRepository.findByLecture(pageable, lecture);

        return teachers.map(TeacherDetailResponseDto::from);
    }

    //강의 강사 임명
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
        sendConfirmMail(lecture.getLectureName(), lecture.getLectureCode(), MemberDto.from(appointedMember));
    }

    public void acceptTeacher(String lectureCode, MemberDto memberDTO) {
        Teacher suggestedTeacher = teacherRepository.findByMemberIdAndLectureCode(memberDTO.getId(), lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TEACHER_ID));

        suggestedTeacher.accept();
    }

    public void rejectTeacher(String lectureCode, MemberDto memberDTO) {
        Teacher suggestedTeacher = teacherRepository.findByMemberIdAndLectureCode(memberDTO.getId(), lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TEACHER_ID));

        teacherRepository.delete(suggestedTeacher);
    }

    //강의 강사 해임
    public void dismissTeacher(String lectureCode, String teacherId, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        checkPermission(lecture, memberDTO);

        Teacher dismissedTeacher = teacherRepository.findByMemberIdAndLectureCode(teacherId, lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TEACHER_ID));

        teacherRepository.delete(dismissedTeacher);
    }

    //강사 확인 메일
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendConfirmMail(String lectureName, String lectureCode, MemberDto memberDto) {
        String subject = "[티미룸]" + lectureName + "에 강사로 제의되었습니다.";
        String content = getConfirmMailContent(lectureCode);
        mailService.sendEmail(memberDto.getEmail(), subject, content, true, false);
    }

    // TODO: 요청을 백엔드로 직접 보내도록 했는데, 프론트가 짜여지면 링크를 수정해야 함.
    private String getConfirmMailContent(String confirmCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='margin:20px;'>");
        sb.append("<p>티미룸 강사 제의 수락하시겠습니까?</p>");
        sb.append("<br>");
        sb.append("<div align='center' style='border:1px solid black; font-family:verdana'>");
        sb.append("<h3>수락 / 거부</h3>");
        sb.append("<div style='font-size:130%'>");
        sb.append("<strong>localhost:8080/api/v1/email/member/confirm/" + confirmCode + "</strong></div><br/>");
        sb.append("<strong>localhost:8080/api/v1/email/member/confirm/" + confirmCode + "</strong></div><br/>");
        sb.append("</div></br></br>");
        sb.append("<p>감사합니다.</p>");
        sb.append("</div>");
        return sb.toString();
    }

}
