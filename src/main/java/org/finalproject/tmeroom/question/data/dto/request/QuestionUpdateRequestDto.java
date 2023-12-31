package org.finalproject.tmeroom.question.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.question.data.entity.Question;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;

@Data
public class QuestionUpdateRequestDto {
    @NotBlank(message = CANNOT_BE_NULL)
    protected String title;
    @NotBlank(message = CANNOT_BE_NULL)
    protected String content;
    protected Boolean isPublic;

    public Question toEntity(Lecture lecture, Member author) {
        return Question.builder()
                .lecture(lecture)
                .author(author)
                .authorNickname(author.getNickname())
                .title(title)
                .content(content)
                .isPublic(isPublic)
                .build();
    }
}
