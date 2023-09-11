package org.finalproject.tmeroom.question.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Question Entity 작성
 */
@Entity
@Getter
public class Question extends BaseTimeEntity {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Member author;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Member lecture;

    @Column
    private String title;

    @Column
    private String content;
}
