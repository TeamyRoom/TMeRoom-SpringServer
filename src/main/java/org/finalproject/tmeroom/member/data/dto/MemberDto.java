package org.finalproject.tmeroom.member.data.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class MemberDto {

    @ToString.Include
    private String id;
    private String pw;
    @ToString.Include
    private String nickname;
    @ToString.Include
    private String email;
    @ToString.Include
    private MemberRole role;

    @Builder
    private MemberDto(String id, String nickname, String pw, String email, MemberRole role) {
        this.id = id;
        this.pw = pw;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .pw(member.getPw())
                .role(member.getRole())
                .build();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add((GrantedAuthority) () -> role.getName());
        return authorities;
    }

    public boolean isAdmin() {
        return role.equals(MemberRole.ADMIN);
    }
}