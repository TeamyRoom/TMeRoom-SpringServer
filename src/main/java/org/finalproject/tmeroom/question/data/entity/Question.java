package org.finalproject.tmeroom.question.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.member.data.Entity.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Question Entity 작성
 */
@Entity
@Getter
@RequiredArgsConstructor
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
    private Member lecture;

    @Column
    @NotNull
    private String title;

    @Column
    @NotNull
    private String content;

    @Column
    private String authorNickname;
}
