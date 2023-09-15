package org.finalproject.tmeroom.lecture.data.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.finalproject.tmeroom.member.data.dto.MemberDto;

@Getter
@Setter
public class LectureUpdateRequestDto {
    String lectureCode;
    String lectureName;
    MemberDto memberDTO;
}