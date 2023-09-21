package org.finalproject.tmeroom.comment.data.dto.request;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;
import static org.finalproject.tmeroom.common.exception.ValidationMessage.COMMENT_OVER_MAX;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentUpdateRequestDto {

    @NotBlank(message = CANNOT_BE_NULL)
    @Max(value = 10000, message = COMMENT_OVER_MAX)
    String content;

    public CommentUpdateRequestDto(String content) {
        this.content = content;
    }
}
