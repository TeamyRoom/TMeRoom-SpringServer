package org.finalproject.tmeroom.question.service;

import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.repository.LectureRepository;
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

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    // 질문 목록 조회
    public Page<QuestionListResponseDto> lookupQuestions(String lectureCode, Pageable pageable) {
        Lecture lecture = lectureRepository.getReferenceById(lectureCode);

        Page<Question> questions = questionRepository.findByLecture(pageable, lecture);

        return questions.map(QuestionListResponseDto::from);
    }

    // 질문 단일 조회
    public QuestionDetailResponseDto readQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow();

        return QuestionDetailResponseDto.from(question);
    }

    // 질문 게시
    public void createQuestion(String lectureCode, MemberDto memberDto, QuestionCreateRequestDto requestDto) {
        Lecture lecture = lectureRepository.findById(lectureCode).orElseThrow();
        Member member = memberRepository.findById(memberDto.getId()).orElseThrow();
        Question question = requestDto.toEntity(lecture, member);

        questionRepository.save(question);
    }

    // 질문 수정
    public void updateQuestion(Long questionId, MemberDto memberDto, QuestionUpdateRequestDto requestDto) {
        Question question = questionRepository.findById(questionId).orElseThrow();

        checkPermission(question, memberDto);

        question.update(requestDto);
    }

    // 질문 삭제
    public void deleteQuestion(Long questionId, MemberDto memberDto) {
        Question question = questionRepository.findById(questionId).orElseThrow();

        checkPermission(question, memberDto);

        questionRepository.delete(question);
    }

    // 질문 공개 여부 수정
    public void openQuestion(Long questionId, MemberDto memberDto) {
        Question question = questionRepository.findById(questionId).orElseThrow();

        checkPermission(question, memberDto);

        question.makePublic();
    }

    private void checkPermission(Question question, MemberDto memberDto) {
        if (memberDto == null || !Objects.equals(question.getAuthor().getId(), memberDto.getId())) {
            throw new ApplicationException(ErrorCode.INVALID_PERMISSION);
        }
    }
}
