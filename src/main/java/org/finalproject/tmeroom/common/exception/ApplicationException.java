package org.finalproject.tmeroom.common.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-12
 * 비정상적인 요청에 대한 응답을 일괄적으로 처리하기 위한 클래스
 * 이 예외를 발생시키면 GlobalControllerAdvice에서 받아 예외를 처리한다
 */
@Getter
@AllArgsConstructor
public class ApplicationException extends RuntimeException {

    private ErrorCode errorCode;
    private HttpStatus status;
    private String message;

    public ApplicationException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.status = errorCode.getStatus();
        this.message = null;
    }

    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        } else {
            return String.format("%s : %s", errorCode.getMessage(), message);
        }
    }
}
