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
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러 발생"),
    REQUEST_PARSE_ERROR(HttpStatus.BAD_REQUEST, "필드 검증 실패"),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "인증 중 오류 발생"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "쿠키에 토큰 없음"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰"),
    AUTHORIZATION_ERROR(HttpStatus.FORBIDDEN, "권한 필요"),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "요청 엔티티 없음"),
    INVALID_PERMISSION(HttpStatus.FORBIDDEN, "소유자 불일치"),
    INVALID_ACCESS_PERMISSION(HttpStatus.FORBIDDEN, "접근 권한 없음"),
    INVALID_READ_QUESTION_PERMISSION(HttpStatus.FORBIDDEN, "질문을 볼 수 있는 권한이 없음"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "일치하는 유저 없음"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 불일치"),
    DUPLICATE_ID(HttpStatus.BAD_REQUEST, "입력한 유저 정보 중복"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "입력한 유저 정보 중복"),
    EMAIL_ALREADY_CONFIRMED(HttpStatus.BAD_REQUEST, "이메일 인증 이미 완료"),
    CODE_NOT_VALID(HttpStatus.BAD_REQUEST, "유효하지 않은 코드"),
    INVALID_ID(HttpStatus.BAD_REQUEST, "아이디 불일치"),
    INVALID_LECTURE_CODE(HttpStatus.BAD_REQUEST, "존재하지 않는 강의 코드"),
    INVALID_QUESTION_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 질문"),
    INVALID_COMMENT_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 댓글"),
    INVALID_TEACHER_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 교사"),
    INVALID_STUDENT_ID(HttpStatus.BAD_REQUEST, "존재하지 않는 학생"),
    TYPE_NOT_CONFIGURED(HttpStatus.INTERNAL_SERVER_ERROR, "검색 타입 미설정");

    private final HttpStatus status;
    private final String message;
}
