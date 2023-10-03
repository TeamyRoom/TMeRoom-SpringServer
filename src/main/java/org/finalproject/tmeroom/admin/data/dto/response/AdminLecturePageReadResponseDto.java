package org.finalproject.tmeroom.admin.data.dto.response;

import lombok.Data;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.springframework.data.domain.Page;

@Data
public class AdminLecturePageReadResponseDto {

    private Page<AdminLectureDetailReadResponseDto> lectures;

    public static AdminLecturePageReadResponseDto of(Page<Lecture> lectures) {
        AdminLecturePageReadResponseDto responseDto = new AdminLecturePageReadResponseDto();
        responseDto.lectures = lectures.map(AdminLectureDetailReadResponseDto::from);
        return responseDto;
    }
}
