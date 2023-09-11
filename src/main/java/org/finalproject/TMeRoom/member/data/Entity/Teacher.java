package org.finalproject.tmeroom.member.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Teacher Entity 작성
 */
@Entity
@Getter
@IdClass(TeacherPK.class)
public class Teacher {
    @Id
    @Column(name = "teacher_id")
    private String teacherId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "teacher_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Id
    @Column(name = "lecture_id")
    private Long lectureId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "lecture_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture;
}
