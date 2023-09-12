package org.finalproject.tmeroom.member.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
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
    @Column(name = "lecture_id")
    private Long lectureId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "lecture_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture;

    @CreatedDate
    @Column(updatable = false, name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
}