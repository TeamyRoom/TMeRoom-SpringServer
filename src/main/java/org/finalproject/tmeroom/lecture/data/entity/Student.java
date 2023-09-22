package org.finalproject.tmeroom.lecture.data.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Student Entity 작성
 */
@Entity
@Getter
@IdClass(StudentPK.class)
@RequiredArgsConstructor
public class Student {
    @Id
    @Column(name = "student_id")
    private String studentId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "student_id")
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

    @CreatedDate
    @Column(updatable = false, name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Builder
    public Student(Member member, Lecture lecture) {
        this.studentId = member.getId();
        this.member = member;
        this.lectureCode = lecture.getLectureCode();
        this.lecture = lecture;
    }

    public void acceptStudent() {
        this.acceptedAt = LocalDateTime.now();
    }
}
