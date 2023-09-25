package org.finalproject.tmeroom.member.data.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.finalproject.tmeroom.member.data.dto.MemberDto;

@Data
@NoArgsConstructor
public class ReadMemberResponseDto {
    private String memberId;
    private String nickname;
    private String email;

    public static ReadMemberResponseDto from(MemberDto member) {
        ReadMemberResponseDto responseDto = new ReadMemberResponseDto();
        responseDto.setMemberId(member.getId());
        responseDto.setNickname(member.getNickname());
        responseDto.setEmail(member.getEmail());
        return responseDto;
    }
}
