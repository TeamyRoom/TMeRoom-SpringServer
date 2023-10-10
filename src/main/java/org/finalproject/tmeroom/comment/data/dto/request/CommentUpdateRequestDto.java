package org.finalproject.tmeroom.comment.data.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.*;

@Data
public class CommentUpdateRequestDto {
    @NotBlank(message = CANNOT_BE_NULL)
    @Size(max = 10000, message = COMMENT_OVER_MAX)
    @Size(min = 5, message = COMMENT_UNDER_MIN)
    String content;
}
