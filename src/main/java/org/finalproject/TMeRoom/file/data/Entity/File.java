package org.finalproject.tmeroom.file.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * File Entity 작성
 */
@Entity
@Getter
public class File extends BaseTimeEntity {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "uploader_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member uploader;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Lecture lecture;

    @Column
    private String fileName;

    @Column
    private String fileLink;
}
