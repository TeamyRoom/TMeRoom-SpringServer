package org.finalproject.tmeroom.member.data.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;
import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.dto.request.MemberUpdateRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Member Entity 작성
 */
@Entity
@Table(indexes = {
        @Index(columnList = "email")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @NotNull
    private String id;

    @NotBlank
    private String pw;

    @NotNull
    private String nickname;

    @Column(unique = true)
    @Email
    @NotNull
    private String email;

    @NotNull
    private MemberRole role;

    @Builder
    public Member(String id, String pw, String nickname, String email, MemberRole role) {
        this.id = id;
        this.pw = pw;
        this.nickname = nickname;
        this.email = email;
        this.role = role == null ? MemberRole.GUEST : role;
    }

    public void confirmEmail() {
        if (!role.equals(MemberRole.GUEST)) {
            throw new ApplicationException(ErrorCode.EMAIL_ALREADY_CONFIRMED);
        }
        role = MemberRole.USER;
    }

    public boolean isIdMatch(String id) {
        return id.equals(this.id);
    }

    public boolean isPasswordMatch(PasswordEncoder encoder, String pw) {
        return encoder.matches(pw, this.pw);
    }

    public void updatePassword(PasswordEncoder encoder, String pw) {
        this.pw = encoder.encode(pw);
    }

    public void updateInfo(MemberUpdateRequestDto requestDto) {
        this.nickname = requestDto.getNickname();
    }
}
