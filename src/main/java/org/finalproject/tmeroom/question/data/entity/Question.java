package org.finalproject.tmeroom.question.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.question.data.dto.request.QuestionUpdateRequestDto;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Question Entity 작성
 */
@Entity
@Table(indexes = {
        @Index(columnList = "lecture_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member author;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture;

    @Column
    @NotNull
    private String title;

    @Column
    @NotNull
    private String content;

    @Column
    private String authorNickname;

    @Column
    private Boolean isPublic;

    @Builder
    public Question(Long id, Member author, @NotNull Lecture lecture, @NotNull String title, @NotNull String content, String authorNickname, Boolean isPublic) {
        this.id = id;
        this.author = author;
        this.lecture = lecture;
        this.title = title;
        this.content = content;
        this.authorNickname = authorNickname;
        this.isPublic = isPublic;
    }

    public void update(QuestionUpdateRequestDto requestDto) {
        this.title = requestDto.getTitle();
        this.content = requestDto.getContent();
        this.isPublic = requestDto.getIsPublic();
    }

    public void makePublic() {
        this.isPublic = true;
    }
}