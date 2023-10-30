package org.finalproject.tmeroom.question.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
import org.finalproject.tmeroom.lecture.repository.StudentRepository;
import org.finalproject.tmeroom.lecture.repository.TeacherRepository;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.member.repository.MemberRepository;
import org.finalproject.tmeroom.question.data.dto.request.QuestionCreateRequestDto;
import org.finalproject.tmeroom.question.data.dto.request.QuestionUpdateRequestDto;
import org.finalproject.tmeroom.question.data.dto.response.QuestionDetailResponseDto;
import org.finalproject.tmeroom.question.data.dto.response.QuestionListResponseDto;
import org.finalproject.tmeroom.question.data.entity.Question;
import org.finalproject.tmeroom.question.repository.QuestionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    // 질문 목록 조회(선생용, 관리자용)
    public Page<QuestionListResponseDto> lookupAllQuestions(String lectureCode, Pageable pageable,
                                                            MemberDto memberDto) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));

        Optional<Teacher> teacher = teacherRepository.findByMemberIdAndLectureCode(memberDto.getId(), lectureCode);
        
        if (teacher.isEmpty() && !lecture.getManager().isIdMatch(memberDto.getId())) {
            throw new ApplicationException(ErrorCode.INVALID_ACCESS_PERMISSION);
        }

        Page<Question> questions = questionRepository.findByLecture(lecture, pageable);

        return questions.map(QuestionListResponseDto::from);
    }


    // 질문 목록 조회(학생용)
    public Page<QuestionListResponseDto> lookupPublicQuestions(String lectureCode, Pageable pageable,
                                                               MemberDto memberDto) {
        Lecture lecture = lectureRepository.getReferenceById(lectureCode);
        Student student = studentRepository.findByMemberIdAndLectureCode(memberDto.getId(), lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_STUDENT_ID));

        if (!student.isAccepted()) {
            throw new ApplicationException(ErrorCode.COURSE_REGISTRATION_NOT_COMPLETED);
        }

        Page<Question> questions =
                questionRepository.findByLectureAndAuthorOrIsPublic(lecture, student.getMember(), true, pageable);

        return questions.map(QuestionListResponseDto::from);
    }

    // 질문 단일 조회
    public QuestionDetailResponseDto readQuestion(String lectureCode, Long questionId, MemberDto memberDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_QUESTION_ID));

        boolean teacherAccepted = isTeacherAndAccepted(memberDto, lectureCode);
        Member author = question.getAuthor();
        Lecture lecture = question.getLecture();

        if (!question.getIsPublic()) {
            if (!teacherAccepted && !author.isIdMatch(memberDto.getId()) && !lecture.getManager().isIdMatch(
                    memberDto.getId())) {
                throw new ApplicationException(ErrorCode.INVALID_READ_QUESTION_PERMISSION);
            }
        } else {
            boolean studentAccepted = isStudentAndAccepted(memberDto, lectureCode);
            if (studentAccepted ||
                    teacherAccepted && author.isIdMatch(memberDto.getId()) && lecture.getManager().isIdMatch(
                            memberDto.getId())) {
                throw new ApplicationException(ErrorCode.INVALID_READ_QUESTION_PERMISSION);
            }
        }

        return QuestionDetailResponseDto.from(question);
    }

    // 질문 게시
    public void createQuestion(String lectureCode, MemberDto memberDto, QuestionCreateRequestDto requestDto) {
        Lecture lecture = lectureRepository.findById(lectureCode)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_LECTURE_CODE));
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        boolean studentAccepted = isStudentAndAccepted(memberDto, lectureCode);
        boolean teacherAccepted = isTeacherAndAccepted(memberDto, lectureCode);

        if (!studentAccepted && !teacherAccepted && member.getRole()!= MemberRole.ADMIN) {
            throw new ApplicationException(ErrorCode.INVALID_ACCESS_PERMISSION);
        }

        Question question = requestDto.toEntity(lecture, member);

        questionRepository.save(question);
    }

    // 질문 수정
    public void updateQuestion(Long questionId, MemberDto memberDto, QuestionUpdateRequestDto requestDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_QUESTION_ID));

        checkPermission(question, memberDto);

        question.update(requestDto);
    }

    // 질문 삭제
    public void deleteQuestion(Long questionId, MemberDto memberDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_QUESTION_ID));
        checkPermission(question, memberDto);

        questionRepository.delete(question);
    }

    // 질문 공개 여부 수정
    public void openQuestion(Long questionId, MemberDto memberDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_QUESTION_ID));
        checkPermission(question, memberDto);

        question.makePublic();
    }

    private void checkPermission(Question question, MemberDto memberDto) {
        if (memberDto == null || !Objects.equals(question.getAuthor().getId(), memberDto.getId())) {
            throw new ApplicationException(ErrorCode.INVALID_PERMISSION);
        }
    }

    private boolean isTeacherAndAccepted(MemberDto memberDto, String lectureCode) {
        return teacherRepository.findByMemberIdAndLectureCode(memberDto.getId(), lectureCode)
                .filter(Teacher::isAccepted)
                .isPresent();
    }

    private boolean isStudentAndAccepted(MemberDto memberDto, String lectureCode) {
        return studentRepository.findByMemberIdAndLectureCode(memberDto.getId(), lectureCode)
                .filter(Student::isAccepted)
                .isPresent();
    }
}
