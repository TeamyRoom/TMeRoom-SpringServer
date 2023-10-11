package org.finalproject.tmeroom.comment.data.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.finalproject.tmeroom.comment.data.entity.Comment;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.question.data.entity.Question;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.COMMENT_OVER_MAX;
import static org.finalproject.tmeroom.common.exception.ValidationMessage.COMMENT_UNDER_MIN;

@Data
public class CommentCreateRequestDto {
    @Size(max = 10000, message = COMMENT_OVER_MAX)
    @Size(min = 2, message = COMMENT_UNDER_MIN)
    private String content;

    public Comment toEntity(Member commenter, Question question) {
        return Comment.builder()
                .commenter(commenter)
                .content(content)
                .question(question)
                .build();
    }
}
