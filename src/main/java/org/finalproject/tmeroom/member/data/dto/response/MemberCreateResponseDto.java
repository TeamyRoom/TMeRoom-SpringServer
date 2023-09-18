package org.finalproject.tmeroom.member.data.dto.response;

import lombok.Builder;
import lombok.Data;
import org.finalproject.tmeroom.member.data.entity.Member;

@Data
@Builder
public class MemberCreateResponseDto {

    private String memberId;
    private String nickname;
    private String email;

    public static MemberCreateResponseDto from(Member member) {
        return MemberCreateResponseDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
