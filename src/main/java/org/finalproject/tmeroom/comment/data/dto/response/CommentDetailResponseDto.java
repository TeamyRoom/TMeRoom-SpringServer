package org.finalproject.tmeroom.comment.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.comment.data.entity.Comment;

import java.time.LocalDateTime;

@Getter
public class CommentDetailResponseDto {
    Long commentId;
    String commenterNickname;
    String content;
    LocalDateTime createdAt;

    @Builder
    private CommentDetailResponseDto(Long commentId, String commenterNickname, String content,
                                     LocalDateTime createdAt) {
        this.commentId = commentId;
        this.commenterNickname = commenterNickname;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static CommentDetailResponseDto from(Comment comment) {
        return CommentDetailResponseDto.builder()
                .commentId(comment.getId())
                .commenterNickname(comment.getCommenter().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreateAt())
                .build();
    }
}
