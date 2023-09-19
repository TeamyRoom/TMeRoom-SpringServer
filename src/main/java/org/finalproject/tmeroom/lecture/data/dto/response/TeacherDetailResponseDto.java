package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Teacher;

@Getter
public class TeacherDetailResponseDto {
    private final String Id;
    private final String nickName;
    private final String Email;

    private TeacherDetailResponseDto(Teacher teacher) {
        this.nickName = teacher.getMember().getNickname();
        this.Id = teacher.getMember().getId();
        this.Email = teacher.getMember().getEmail();
    }

    public static TeacherDetailResponseDto from(Teacher teacher) {
        return new TeacherDetailResponseDto(teacher);
    }
}
