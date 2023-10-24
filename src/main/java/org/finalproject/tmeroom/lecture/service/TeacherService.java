package org.finalproject.tmeroom.lecture.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.admin.constant.MemberSearchType;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberPageReadResponseDto;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.common.service.MailService;
import org.finalproject.tmeroom.lecture.data.dto.request.AppointTeacherRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.request.TeacherSearchRequestDto;
import org.finalproject.tmeroom.lecture.data.dto.response.TeacherDetailResponseDto;
import org.finalproject.tmeroom.lecture.data.dto.response.TeacherMemberPageReadResponseDto;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${spring.config.host}")
    private String host_url;

    //강의에 임명될 강사 검색
    public TeacherMemberPageReadResponseDto searchMembers(TeacherSearchRequestDto requestDto, MemberDto memberDto) {
        Lecture lecture = lectureRepository.findById(requestDto.getLectureCode())
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        checkPermission(lecture, memberDto);

        MemberSearchType searchType = requestDto.getSearchType();
        String keyword = requestDto.getKeyword();
        Pageable pageable = requestDto.getPageable();

        Page<Member> members = findMembersByKeyword(lecture, searchType, keyword, pageable);
        return TeacherMemberPageReadResponseDto.of(members);
    }

    private Page<Member> findMembersByKeyword(Lecture lecture, MemberSearchType searchType, String keyword,
                                              Pageable pageable) {
        //TODO: Teacher랑 조인해서 Teacher Table에 존재하지 않는 멤버만 가져오도록 변경해야함
        return switch (searchType) {
            case ID -> memberRepository.findAllByIdContaining(keyword, pageable);
            case EMAIL -> memberRepository.findAllByEmailContaining(keyword, pageable);
            default -> throw new ApplicationException(ErrorCode.TYPE_NOT_CONFIGURED);
        };
    }

    //전체 강사 조회
    public Page<TeacherDetailResponseDto> lookupTeachers(String lectureCode, MemberDto memberDto,
                                                                 Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        checkPermission(lecture, memberDto);

        Page<Teacher> teachers = teacherRepository.findByLecture(lecture, pageable);

        return teachers.map(TeacherDetailResponseDto::from);
    }

    //강의에 임명된 강사 조회
    public Page<TeacherDetailResponseDto> lookupAcceptedTeachers(String lectureCode, MemberDto memberDto,
                                                                 Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        checkPermission(lecture, memberDto);

        Page<Teacher> teachers = teacherRepository.findByLectureAndAcceptedAtNotNull(lecture, pageable);

        return teachers.map(TeacherDetailResponseDto::from);
    }

    //강의에 초대중인 강사 조회
    public Page<TeacherDetailResponseDto> lookupUnAcceptedTeachers(String lectureCode, MemberDto memberDto,
                                                                   Pageable pageable) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        checkPermission(lecture, memberDto);

        Page<Teacher> teachers = teacherRepository.findByLectureAndAcceptedAtIsNull(lecture, pageable);

        return teachers.map(TeacherDetailResponseDto::from);
    }

    //강의 강사 임명(관리자 요청)
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

    //강의 강사 해임(관리자 요청)
    public void dismissTeacher(String lectureCode, String teacherId, MemberDto memberDTO) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        checkPermission(lecture, memberDTO);

        Teacher dismissedTeacher = teacherRepository.findByMemberIdAndLectureCode(teacherId, lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TEACHER_ID));

        teacherRepository.delete(dismissedTeacher);
    }

    // 강사 임명 수락(강사 요청)
    public void acceptTeacher(String lectureCode, MemberDto memberDTO) {
        Teacher suggestedTeacher = teacherRepository.findByMemberIdAndLectureCode(memberDTO.getId(), lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TEACHER_ID));

        suggestedTeacher.accept();
    }

    // 강사 임명 거부(강사 요청)
    public void rejectTeacher(String lectureCode, MemberDto memberDTO) {
        Teacher suggestedTeacher = teacherRepository.findByMemberIdAndLectureCode(memberDTO.getId(), lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_TEACHER_ID));

        teacherRepository.delete(suggestedTeacher);
    }

    //강사 확인 메일
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendConfirmMail(String lectureName, String lectureCode, MemberDto memberDto) {
        String subject = "[티미룸]" + lectureName + "에 강사로 제의되었습니다.";
        String content = getConfirmMailContent(lectureCode);
        mailService.sendEmail(memberDto.getEmail(), subject, content, true, false);
    }

    private String getConfirmMailContent(String confirmCode) {
        String sb = "<div style='margin:20px;'>" +
                "<br>" +
                "<div align='center' style='border:1px solid black; font-family:verdana'>" +
                "<p>티미룸 강사 제의를 수락하시겠습니까?</p>" +
                "<div style='font-size:130%'>" +
                "<a style='display: inline-block; width: calc(50% - 5px); height: 45px; max-width: 280px; margin-right: 10px; background-color: #bdc3c7; font-size: 15px; color: #fff; text-align: center; line-height: 45px; vertical-align: top;' " +
                "href='" + host_url + "/accpet-teacher-request/" + confirmCode + "'> 수락 </a>" +
                "<a style='display: inline-block; width: calc(50% - 5px); height: 45px; max-width: 280px; margin-right: 10px; background-color: #bdc3c7; font-size: 15px; color: #fff; text-align: center; line-height: 45px; vertical-align: top;' " +
                "href='" + host_url + "/denied-teacher-request/" + confirmCode + "'> 거부 </a>" +
                "<p>감사합니다.</p>" +
                "</div><br/></div></br></br>" +
                "</div>";
        return sb;
    }

}
