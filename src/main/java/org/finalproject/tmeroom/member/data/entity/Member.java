package org.finalproject.tmeroom.member.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Member Entity 작성
 */
@Entity
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

}
