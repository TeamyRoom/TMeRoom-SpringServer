package org.finalproject.tmeroom.common.data.dto;

import lombok.Getter;
import org.springframework.validation.FieldError;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-20
 * 검증 실패시 실패한 필드명과 실패 메시지를 담음
 */
@Getter
public class ValidationMessageDto {

    String field;
    String message;

    private ValidationMessageDto(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public static ValidationMessageDto from(FieldError fieldError) {
        return new ValidationMessageDto(fieldError.getField(), fieldError.getDefaultMessage());
    }
}