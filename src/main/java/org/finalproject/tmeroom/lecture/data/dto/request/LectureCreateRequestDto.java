package org.finalproject.tmeroom.lecture.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.LECTURE_UNDER_MIN;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class LectureCreateRequestDto {
    @NotBlank(message = LECTURE_UNDER_MIN)
    protected String lectureName;
    protected MemberDto memberDTO;

    public Lecture toEntity(String lectureCode, Member manager) {
        return Lecture.builder()
                .lectureCode(lectureCode)
                .lectureName(this.lectureName)
                .manager(manager)
                .build();
    }
}
