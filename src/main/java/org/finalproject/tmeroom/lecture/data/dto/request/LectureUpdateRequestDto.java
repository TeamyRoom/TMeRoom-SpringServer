package org.finalproject.tmeroom.lecture.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.finalproject.tmeroom.member.data.dto.MemberDto;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;

@Getter
@Setter
public class LectureUpdateRequestDto {
    String lectureCode;
    String lectureName;
    MemberDto memberDTO;
}