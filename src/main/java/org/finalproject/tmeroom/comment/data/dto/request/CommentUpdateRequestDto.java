package org.finalproject.tmeroom.comment.data.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.COMMENT_OVER_MAX;
import static org.finalproject.tmeroom.common.exception.ValidationMessage.COMMENT_UNDER_MIN;

@Data
public class CommentUpdateRequestDto {
    @Size(max = 10000, message = COMMENT_OVER_MAX)
    @Size(min = 5, message = COMMENT_UNDER_MIN)
    private String content;
}
