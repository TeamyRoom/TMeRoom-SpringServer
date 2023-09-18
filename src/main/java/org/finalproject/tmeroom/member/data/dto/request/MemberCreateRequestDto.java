package org.finalproject.tmeroom.member.data.dto.request;

import lombok.Builder;
import lombok.Data;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@Builder
public class MemberCreateRequestDto {

    private String memberId;
    private String password;
    private String nickname;
    private String email;

    public Member toEntity(PasswordEncoder encoder) {
        return Member.builder()
                .id(memberId)
                .pw(encoder.encode(password))
                .nickname(nickname)
                .email(email)
                .build();
    }

}
