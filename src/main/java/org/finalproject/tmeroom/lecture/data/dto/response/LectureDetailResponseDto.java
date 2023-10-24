package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Student;

import java.time.LocalDateTime;

@Getter
public class LectureDetailResponseDto {
    String lectureCode;
    String lectureName;
    LocalDateTime acceptedAt;

    @Builder
    public LectureDetailResponseDto(String lectureCode, String lectureName, LocalDateTime acceptedAt) {
        this.lectureCode = lectureCode;
        this.lectureName = lectureName;
        this.acceptedAt = acceptedAt;
    }

    public static LectureDetailResponseDto from(Student student) {
        return LectureDetailResponseDto.builder()
                .lectureCode(student.getLectureCode())
                .lectureName(student.getLecture().getLectureName())
                .acceptedAt(student.getAcceptedAt())
                .build();
    }
}
