package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Student;

import java.time.LocalDateTime;

@Getter
public class StudentDetailResponseDto {
    private final String id;
    private final String nickName;
    private final String email;
    private final LocalDateTime appliedAt;
    private final LocalDateTime acceptedAt;

    @Builder
    public StudentDetailResponseDto(String id, String nickName, String email,
                                    LocalDateTime appliedAt, LocalDateTime acceptedAt) {
        this.id = id;
        this.nickName = nickName;
        this.email = email;
        this.appliedAt = appliedAt;
        this.acceptedAt = acceptedAt;
    }

    public static StudentDetailResponseDto from(Student student) {
        return StudentDetailResponseDto.builder()
                .id(student.getMember().getId())
                .nickName(student.getMember().getNickname())
                .appliedAt(student.getAppliedAt())
                .acceptedAt(student.getAcceptedAt())
                .build();
    }
}
