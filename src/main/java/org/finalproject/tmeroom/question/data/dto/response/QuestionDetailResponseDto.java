package org.finalproject.tmeroom.question.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.question.data.entity.Question;

import java.time.LocalDateTime;

@Getter
public class QuestionDetailResponseDto {
    Long questionId;
    String questionTitle;
    String questionContent;
    String authorNickname;
    LocalDateTime createdAt;


    @Builder
    public QuestionDetailResponseDto(Long questionId, String questionTitle, String questionContent,
                                     String authorNickname, LocalDateTime createdAt) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionContent = questionContent;
        this.authorNickname = authorNickname;
        this.createdAt = createdAt;
    }

    public static QuestionDetailResponseDto from(Question question) {
        return QuestionDetailResponseDto.builder()
                .questionId(question.getId())
                .questionTitle(question.getTitle())
                .questionContent(question.getContent())
                .authorNickname(question.getAuthorNickname())
                .createdAt(question.getCreatedAt())
                .build();
    }
}
