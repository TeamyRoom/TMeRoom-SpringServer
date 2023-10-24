package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.lecture.data.entity.Student;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;

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

    public static LectureDetailResponseDto fromStudent(Student student) {
        return LectureDetailResponseDto.builder()
                .lectureCode(student.getLectureCode())
                .lectureName(student.getLecture().getLectureName())
                .acceptedAt(student.getAcceptedAt())
                .build();
    }

    public static LectureDetailResponseDto fromTeacher(Teacher teacher) {
        return LectureDetailResponseDto.builder()
                .lectureCode(teacher.getLectureCode())
                .lectureName(teacher.getLecture().getLectureName())
                .acceptedAt(teacher.getAcceptedAt())
                .build();
    }

    public static LectureDetailResponseDto fromManager(Lecture lecture) {
        return LectureDetailResponseDto.builder()
                .lectureCode(lecture.getLectureCode())
                .lectureName(lecture.getLectureName())
                .build();
    }
}
