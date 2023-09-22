package org.finalproject.tmeroom.auth.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;

@Data
public class LoginRequestDto {

    @NotBlank(message = CANNOT_BE_NULL)
    private String id;
    @NotBlank(message = CANNOT_BE_NULL)
    private String pw;
}
