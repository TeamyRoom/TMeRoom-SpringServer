package org.finalproject.tmeroom.member.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import org.finalproject.tmeroom.common.data.entity.BaseTimeEntity;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * Member Entity 작성
 */
@Entity
@Getter
public class Member extends BaseTimeEntity {
    @Id
    private String id;

    private String pw;

    private String nickname;

    @Column(unique = true)
    @Email
    private String email;

    private String role;

}
