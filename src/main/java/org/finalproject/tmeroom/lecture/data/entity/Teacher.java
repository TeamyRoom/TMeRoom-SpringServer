package org.finalproject.tmeroom.lecture.data.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.member.data.entity.Member;
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
@RequiredArgsConstructor
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
    @Column(name = "lecture_code")
    private String lectureCode;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "lecture_code")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture;

    @Builder
    public Teacher(Member member, Lecture lecture){
        this.teacherId = member.getId();
        this.lectureCode = lecture.getLectureCode();
    }
}
