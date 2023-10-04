package org.finalproject.tmeroom.admin.data.dto.response;

import lombok.Data;
import org.finalproject.tmeroom.member.data.entity.Member;

@Data
public class AdminMemberDetailReadResponseDto {

    private String memberId;
    private String nickname;
    private String email;

    public static AdminMemberDetailReadResponseDto from(Member member) {
        AdminMemberDetailReadResponseDto responseDto = new AdminMemberDetailReadResponseDto();
        responseDto.setMemberId(member.getId());
        responseDto.setNickname(member.getNickname());
        responseDto.setEmail(member.getEmail());
        return responseDto;
    }
}
