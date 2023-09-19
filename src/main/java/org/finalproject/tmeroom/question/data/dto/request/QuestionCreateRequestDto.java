package org.finalproject.tmeroom.question.data.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.question.data.entity.Question;

@Getter
@Setter
public class QuestionCreateRequestDto {
    protected String title;
    protected String content;
    protected Boolean isPublic;

    public QuestionCreateRequestDto(String title, String content, Boolean isPublic) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
    }

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
