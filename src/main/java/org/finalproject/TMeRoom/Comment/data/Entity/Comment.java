package org.finalproject.tmeroom.Comment.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.finalproject.tmeroom.question.data.entity.Question;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Comment Entity 작성
 */
@Entity
@Getter
public class Comment extends BaseTimeEntity {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "commenter_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member commenter;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Question question;
}
