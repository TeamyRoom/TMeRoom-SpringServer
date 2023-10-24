package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Data;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.data.domain.Page;

@Data
public class TeacherMemberPageReadResponseDto {

    private Page<TeacherMemberDetailReadResponseDto> members;

    public static TeacherMemberPageReadResponseDto of(Page<Member> members) {
        TeacherMemberPageReadResponseDto responseDto = new TeacherMemberPageReadResponseDto();
        responseDto.members = members.map(TeacherMemberDetailReadResponseDto::from);
        return responseDto;
    }
}
