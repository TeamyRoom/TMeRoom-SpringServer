package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;

@Getter
public class LectureCreateResponseDto {
    String lectureCode;

    @Builder
    public LectureCreateResponseDto(String lectureCode) {
        this.lectureCode = lectureCode;
    }

    public static LectureCreateResponseDto from(Lecture lecture) {
        return LectureCreateResponseDto.builder()
                .lectureCode(lecture.getLectureName())
                .build();
    }
}
