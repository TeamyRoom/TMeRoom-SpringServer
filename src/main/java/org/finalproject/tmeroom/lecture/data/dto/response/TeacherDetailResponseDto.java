package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;

import java.time.LocalDateTime;

@Getter
public class TeacherDetailResponseDto {
    private final String Id;
    private final String nickName;
    private final String Email;
    private final LocalDateTime suggestedAt;
    private final LocalDateTime acceptedAt;

    private TeacherDetailResponseDto(Teacher teacher) {
        this.nickName = teacher.getMember().getNickname();
        this.Id = teacher.getMember().getId();
        this.Email = teacher.getMember().getEmail();
        this.suggestedAt = teacher.getSuggestedAt();
        this.acceptedAt = teacher.getAcceptedAt();
    }

    public static TeacherDetailResponseDto from(Teacher teacher) {
        return new TeacherDetailResponseDto(teacher);
    }
}
