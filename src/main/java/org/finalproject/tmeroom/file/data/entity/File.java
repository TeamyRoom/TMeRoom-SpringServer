package org.finalproject.tmeroom.file.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
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
    private String uuidFileName;

    @NotNull
    private String fileLink;

    @Column
    private String uploaderNickname;

    @Column
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Builder
    public File(Lecture lecture, String fileName, String uuidFileName, String fileLink, String uploaderNickname, FileType fileType) {
        this.lecture = lecture;
        this.fileName = fileName;
        this.uuidFileName = uuidFileName;
        this.fileLink = fileLink;
        this.uploaderNickname = uploaderNickname;
        this.fileType = fileType;
    }
}
