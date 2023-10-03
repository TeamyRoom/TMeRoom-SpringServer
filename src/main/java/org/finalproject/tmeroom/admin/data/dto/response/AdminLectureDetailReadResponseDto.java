package org.finalproject.tmeroom.admin.data.dto.response;

import lombok.Data;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;

@Data
public class AdminLectureDetailReadResponseDto {

    private String lectureCode;
    private String lectureName;
    private String managerId;

    public static AdminLectureDetailReadResponseDto from(Lecture lecture) {
        AdminLectureDetailReadResponseDto lectureInfo = new AdminLectureDetailReadResponseDto();
        lectureInfo.setLectureCode(lecture.getLectureCode());
        lectureInfo.setLectureName(lecture.getLectureName());
        lectureInfo.setManagerId(lecture.getManager().getId());
        return lectureInfo;
    }
}
