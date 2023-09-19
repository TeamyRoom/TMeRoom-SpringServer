package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Student;

@Getter
public class LectureDetailResponseDto {
    String lectureCode;
    String lectureName;

    @Builder
    public LectureDetailResponseDto(String lectureCode, String lectureName) {
        this.lectureCode = lectureCode;
        this.lectureName = lectureName;
    }

    public static LectureDetailResponseDto from(Student student) {
        return LectureDetailResponseDto.builder()
                .lectureCode(student.getLectureCode())
                .lectureName(student.getLecture().getLectureName())
                .build();
    }
}
