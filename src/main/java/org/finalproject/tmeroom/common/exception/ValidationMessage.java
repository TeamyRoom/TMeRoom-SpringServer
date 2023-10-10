package org.finalproject.tmeroom.common.exception;

public class ValidationMessage {
    public static final String CANNOT_BE_NULL = "필수로 입력되어야 합니다.";
    public static final String UNMATCHED_USERNAME = "아이디는 4자 이상, 15자 이하의 소문자, 숫자 혹은 \"_\" 의 조합이어야 합니다.";
    public static final String UNMATCHED_PASSWORD = "비밀번호는 6자 이상, 100자 이하의 알파벳, 숫자의 조합이어야 합니다.";
    public static final String UNMATCHED_EMAIL = "형식에 맞는 이메일 주소여야 합니다.";
    public static final String UNMATCHED_NICKNAME = "닉네임은 20자 이하이어야 합니다.";
    public static final String COMMENT_OVER_MAX = "댓글은 만 자 이상 적을 수 없습니다.";
    public static final String COMMENT_UNDER_MIN = "댓글은 1 자 이상 적어야 합니다.";
}
