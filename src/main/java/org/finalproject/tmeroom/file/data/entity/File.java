package org.finalproject.tmeroom.file.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.lecture.data.Entity.Lecture;
import org.finalproject.tmeroom.member.data.Entity.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * File Entity 작성
 */
@Entity
@Getter
@RequiredArgsConstructor
public class File extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture;

    @NotNull
    private String fileName;

    @NotNull
    private String fileLink;

    @Column
    private String uploaderNickname;

    @Column
    private String fileType;
}
