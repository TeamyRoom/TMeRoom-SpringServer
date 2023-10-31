package org.finalproject.tmeroom.lecture.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.finalproject.tmeroom.member.data.dto.MemberDto;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;
import static org.finalproject.tmeroom.common.exception.ValidationMessage.LECTURE_UNDER_MIN;

@Data
public class LectureUpdateRequestDto {
    String lectureCode;
    @NotBlank(message = LECTURE_UNDER_MIN)
    String lectureName;
    MemberDto memberDto;
}