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
    INVALID_PERMISSION(HttpStatus.FORBIDDEN, "소유자 불일치"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_ID(HttpStatus.BAD_REQUEST, "입력한 유저 정보 중복"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "입력한 유저 정보 중복")
    //    ==== 여기까지 작성 ====
    ;

    private final HttpStatus status;
    private final String message;
}
