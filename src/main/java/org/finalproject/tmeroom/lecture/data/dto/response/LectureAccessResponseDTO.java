package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LectureAccessResponseDTO {
    String lectureName;
    String nickName;
    String role;

    @Builder
    public LectureAccessResponseDTO(String lectureName, String nickName, String role) {
        this.lectureName = lectureName;
        this.nickName = nickName;
        this.role = role;
    }
}
