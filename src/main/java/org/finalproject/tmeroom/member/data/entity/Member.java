package org.finalproject.tmeroom.member.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Member Entity 작성
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
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
    private String role;

}
