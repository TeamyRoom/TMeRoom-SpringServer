package org.finalproject.tmeroom.admin.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.admin.constant.LectureSearchType;
import org.finalproject.tmeroom.admin.constant.MemberRoleSearchType;
import org.finalproject.tmeroom.admin.constant.MemberSearchType;
import org.finalproject.tmeroom.admin.data.dto.request.*;
import org.finalproject.tmeroom.admin.data.dto.response.AdminLectureDetailReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminLecturePageReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberDetailReadResponseDto;
import org.finalproject.tmeroom.admin.data.dto.response.AdminMemberPageReadResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;


    private Member getMemberById(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
    }

    private void checkAdmin(MemberDto loginMember) {
        if (!loginMember.isAdmin()) {
            throw new ApplicationException(ErrorCode.AUTHORIZATION_ERROR);
        }
    }

    private Page<Member> findMembersByKeyword(MemberSearchType searchType, String keyword, Pageable pageable) {
        return switch (searchType) {
            case ID -> memberRepository.findAllByIdContaining(keyword, pageable);
            case EMAIL -> memberRepository.findAllByEmailContaining(keyword, pageable);
            default -> throw new ApplicationException(ErrorCode.TYPE_NOT_CONFIGURED);
        };
    }

    private Page<Lecture> findLecturesByMember(MemberRoleSearchType searchType, String memberId, Pageable pageable) {
        return switch (searchType) {
            case STUDENT -> studentRepository.findAllByMemberId(memberId, pageable).map(Student::getLecture);
            case TEACHER -> teacherRepository.findAllByMemberId(memberId, pageable).map(Teacher::getLecture);
            case MANAGER -> lectureRepository.findAllByManagerId(memberId, pageable).map(Function.identity());
            default -> throw new ApplicationException(ErrorCode.TYPE_NOT_CONFIGURED);
        };
    }

    private Page<Lecture> findLecturesByKeyword(LectureSearchType searchType, String keyword, Pageable pageable) {
        return switch (searchType) {
            case NAME -> lectureRepository.findAllByLectureNameContaining(keyword, pageable);
            case MANAGER -> lectureRepository.findAllByManagerIdContaining(keyword, pageable);
            default -> throw new ApplicationException(ErrorCode.TYPE_NOT_CONFIGURED);
        };
    }

    public AdminMemberPageReadResponseDto searchMembers(AdminMemberSearchRequestDto requestDto, MemberDto loginMember) {
        checkAdmin(loginMember);

        MemberSearchType searchType = requestDto.getSearchType();
        String keyword = requestDto.getKeyword();
        Pageable pageable = requestDto.getPageable();

        Page<Member> members = findMembersByKeyword(searchType, keyword, pageable);
        return AdminMemberPageReadResponseDto.of(members);
    }

    public AdminLecturePageReadResponseDto searchLectures(AdminLectureSearchRequestDto requestDto,
                                                          MemberDto loginMember) {
        checkAdmin(loginMember);

        LectureSearchType searchType = requestDto.getSearchType();
        String keyword = requestDto.getKeyword();
        Pageable pageable = requestDto.getPageable();

        Page<Lecture> lectures = findLecturesByKeyword(searchType, keyword, pageable);
        return AdminLecturePageReadResponseDto.of(lectures);
    }

    public AdminMemberDetailReadResponseDto readMemberDetailProfile(AdminMemberDetailProfileRequestDto requestDto,
                                                                    MemberDto loginMember) {
        checkAdmin(loginMember);

        Member foundMember = getMemberById(requestDto.getMemberId());
        return AdminMemberDetailReadResponseDto.from(foundMember);
    }

    public AdminLecturePageReadResponseDto readMemberDetailLecture(AdminMemberDetailLectureRequestDto requestDto,
                                                                   MemberDto loginMember) {
        checkAdmin(loginMember);

        Member foundMember = getMemberById(requestDto.getMemberId());
        String memberId = foundMember.getId();
        MemberRoleSearchType searchType = requestDto.getSearchType();
        Pageable pageable = requestDto.getPageable();

        Page<Lecture> lectures = findLecturesByMember(searchType, memberId, pageable);
        return AdminLecturePageReadResponseDto.of(lectures);
    }

    public AdminLectureDetailReadResponseDto readLectureInfo(AdminLectureDetailRequestDto requestDto,
                                                             MemberDto loginMember) {
        checkAdmin(loginMember);

        Lecture lecture = lectureRepository.findById(requestDto.getLectureCode())
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        return AdminLectureDetailReadResponseDto.from(lecture);
    }

    public void deleteMember(String memberId, MemberDto loginMember) {
        checkAdmin(loginMember);

        memberRepository.deleteById(memberId);
    }

    public void deleteLecture(String lectureCode, MemberDto loginMember) {
        checkAdmin(loginMember);

        lectureRepository.deleteById(lectureCode);
    }
}
