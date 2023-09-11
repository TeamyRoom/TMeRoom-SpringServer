package org.finalproject.tmeroom.lecture.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Lecture Entity 작성
 */
@Entity
@Getter
public class Lecture extends BaseTimeEntity {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member manager;

    @Column
    private String LectureName;
}
