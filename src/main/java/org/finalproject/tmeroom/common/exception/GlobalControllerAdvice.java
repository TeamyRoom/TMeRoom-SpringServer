package org.finalproject.tmeroom.common.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.finalproject.tmeroom.common.data.dto.Response;
import org.finalproject.tmeroom.common.data.dto.ValidationMessageDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-12
 * 응답 처리 중 발생하는 예외를 일괄적으로 처리
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("Error occurs: {}", e.toString());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(Response.error(ErrorCode.INTERNAL_SERVER_ERROR.name(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> applicationExceptionHandler(ApplicationException e) {
        return ResponseEntity.status(e.getStatus())
                .body(Response.error(e.getErrorCode().name(), e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFoundExceptionHandler(EntityNotFoundException e) {
        return ResponseEntity.status(ErrorCode.ENTITY_NOT_FOUND.getStatus())
                .body(Response.error(ErrorCode.ENTITY_NOT_FOUND.name(), e.getMessage()));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> validExceptionHandler(MethodArgumentNotValidException e) {

        // 파싱 중 검증에 실패한 필드의 실패 메시지를 필드별로 모읍니다.
        List<ValidationMessageDto> messages = e.getFieldErrors().stream()
                .map(ValidationMessageDto::from)
                .toList();

        return ResponseEntity.status(ErrorCode.REQUEST_PARSE_ERROR.getStatus())
                .body(Response.error(ErrorCode.REQUEST_PARSE_ERROR.name(), messages));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> enumConversionExceptionHandler(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(ErrorCode.PARAM_CONVERSION_FAILURE.getStatus())
                .body(Response.error(ErrorCode.PARAM_CONVERSION_FAILURE.name(), ErrorCode.PARAM_CONVERSION_FAILURE.getMessage() + ": " + e.getName()));
    }
}

