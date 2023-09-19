package org.finalproject.tmeroom.question.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.question.data.entity.Question;

@Getter
public class QuestionListResponseDto {
    Long questionId;
    String questionTitle;

    @Builder
    private QuestionListResponseDto(Long questionId, String questionTitle) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
    }

    public static QuestionListResponseDto from(Question question) {
        return QuestionListResponseDto.builder()
                .questionId(question.getId())
                .questionTitle(question.getTitle())
                .build();
    }
}
