package org.finalproject.tmeroom.lecture.data.dto.response;

import lombok.Data;
import org.finalproject.tmeroom.member.data.entity.Member;

@Data
public class TeacherMemberDetailReadResponseDto {

    private String memberId;
    private String nickname;
    private String email;

    public static TeacherMemberDetailReadResponseDto from(Member member) {
        TeacherMemberDetailReadResponseDto responseDto = new TeacherMemberDetailReadResponseDto();
        responseDto.setMemberId(member.getId());
        responseDto.setNickname(member.getNickname());
        responseDto.setEmail(member.getEmail());
        return responseDto;
    }
}
