package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Student;

import java.time.LocalDateTime;

@Getter
public class StudentDetailResponseDto {
    String memberNickname;
    String lectureCode;
    String lectureName;
    LocalDateTime appliedAt;
    LocalDateTime acceptedAt;

    @Builder
    public StudentDetailResponseDto(String memberNickname, String lectureCode, String lectureName,
                                    LocalDateTime appliedAt, LocalDateTime acceptedAt) {
        this.memberNickname = memberNickname;
        this.lectureCode = lectureCode;
        this.lectureName = lectureName;
        this.appliedAt = appliedAt;
        if (acceptedAt != null) this.acceptedAt = acceptedAt;
    }

    public static StudentDetailResponseDto from(Student student) {
        return StudentDetailResponseDto.builder()
                .memberNickname(student.getMember().getNickname())
                .lectureCode(student.getLectureCode())
                .lectureName(student.getLecture().getLectureName())
                .appliedAt(student.getAppliedAt())
                .acceptedAt(student.getAcceptedAt())
                .build();
    }
}
