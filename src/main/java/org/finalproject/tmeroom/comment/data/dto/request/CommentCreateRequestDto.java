package org.finalproject.tmeroom.comment.data.dto.request;

import lombok.Getter;
import org.finalproject.tmeroom.comment.data.entity.Comment;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.question.data.entity.Question;

@Getter
public class CommentCreateRequestDto {
    String content;

    public CommentCreateRequestDto(String content) {
        this.content = content;
    }

    public Comment toEntity(Member commenter, Question question){
        return Comment.builder()
                .commenter(commenter)
                .content(content)
                .question(question)
                .build();
    }
}
