package org.finalproject.tmeroom.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-12
 * 에러 코드와 에러 메시지를 관리하기 위한 Enum 클래스
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    //    ==== 여기서부터 작성 ====
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러 발생"),
    REQUEST_PARSE_ERROR(HttpStatus.BAD_REQUEST, "필드 검증 실패"),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "인증 중 오류 발생"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "쿠키에 토큰 없음"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰"),
    AUTHORIZATION_ERROR(HttpStatus.FORBIDDEN, "권한 필요"),
    INVALID_PERMISSION(HttpStatus.FORBIDDEN, "소유자 불일치"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_ID(HttpStatus.BAD_REQUEST, "입력한 유저 정보 중복"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "입력한 유저 정보 중복"),
    EMAIL_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "이메일 인증 이미 완료"),
    CODE_NOT_VALID(HttpStatus.BAD_REQUEST, "유효하지 않은 코드"),
    INVALID_ID(HttpStatus.BAD_REQUEST, "아이디 불일치"),
    INVALID_LECTURE_CODE(HttpStatus.BAD_REQUEST, "존재하지 않는 강의 코드입니다."),
    INVALID_QUESTION_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 질문입니다."),
    INVALID_COMMENT_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 댓글입니다."),
    INVALID_TEACHER_ID(HttpStatus.BAD_REQUEST, "강의에 초빙되지 않은 교사입니다."),
    //    ==== 여기까지 작성 ====
    ;

    private final HttpStatus status;
    private final String message;
}
