package org.finalproject.tmeroom.comment.data.dto.request;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;
import static org.finalproject.tmeroom.common.exception.ValidationMessage.COMMENT_OVER_MAX;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.finalproject.tmeroom.comment.data.entity.Comment;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.question.data.entity.Question;

@Getter
public class CommentCreateRequestDto {

    @NotBlank(message = CANNOT_BE_NULL)
    @Max(value = 10000, message = COMMENT_OVER_MAX)
    String content;

    public CommentCreateRequestDto(String content) {
        this.content = content;
    }

    public Comment toEntity(Member commenter, Question question) {
        return Comment.builder()
                .commenter(commenter)
                .content(content)
                .question(question)
                .build();
    }
}
