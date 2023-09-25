package org.finalproject.TMeRoom.common.util;

import jakarta.annotation.PostConstruct;
import org.finalproject.tmeroom.member.constant.MemberRole;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestConfiguration
public class MockMemberProvider {

    @MockBean
    private PasswordEncoder encoderBean;

    private static PasswordEncoder passwordEncoder;

    @PostConstruct
    private void init() {
        given(encoderBean.encode(any(String.class))).willReturn("encodedPassword");
        passwordEncoder = encoderBean;
    }

    public static Member getMockGuestMember() {
        return Member.builder()
                .id("testGuest")
                .pw("password")
                .email("testGuest@test.com")
                .nickname("testGuest")
                .role(MemberRole.GUEST)
                .encoder(passwordEncoder)
                .build();
    }

    public static Member getMockUserMember() {
        return Member.builder()
                .id("testUser")
                .pw("password")
                .email("testUser@test.com")
                .nickname("testUser")
                .role(MemberRole.USER)
                .encoder(passwordEncoder)
                .build();
    }

    public static Member getMockManagerMember() {
        return Member.builder()
                .id("manager")
                .pw("password")
                .email("testManager@test.com")
                .nickname("manager")
                .role(MemberRole.USER)
                .encoder(passwordEncoder)
                .build();
    }

    public static Member getMockStudentMember() {
        return Member.builder()
                .id("student")
                .pw("password")
                .email("testStudent@test.com")
                .nickname("student")
                .role(MemberRole.USER)
                .encoder(passwordEncoder)
                .build();
    }

    public static Member getMockTeacherMember() {
        return Member.builder()
                .id("teacher")
                .pw("encodedPw")
                .email("testGuest@test.com")
                .nickname("teacher")
                .role(MemberRole.USER)
                .encoder(passwordEncoder)
                .build();
    }

    public static Member getMockAnonymousMember() {
        return Member.builder()
                .id("anonymous")
                .pw("encodedPw")
                .email("testGuest@test.com")
                .nickname("anonymous")
                .role(MemberRole.USER)
                .encoder(passwordEncoder)
                .build();
    }
}
