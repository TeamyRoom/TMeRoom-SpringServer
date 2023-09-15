package org.finalproject.tmeroom.lecture.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.dto.MemberDto;
import org.finalproject.tmeroom.member.data.entity.Member;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class LectureCreateRequestDto {
    @NotBlank
    protected String lectureCode;
    @NotBlank
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
