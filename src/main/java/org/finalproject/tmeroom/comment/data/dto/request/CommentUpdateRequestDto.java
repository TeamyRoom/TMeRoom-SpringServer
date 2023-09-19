package org.finalproject.tmeroom.comment.data.dto.request;

import lombok.Getter;

@Getter
public class CommentUpdateRequestDto {
    String content;

    public CommentUpdateRequestDto(String content) {
        this.content = content;
    }
}
