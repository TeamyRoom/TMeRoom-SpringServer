package org.finalproject.tmeroom.auth.data.dto.request;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {

    @NotBlank(message = CANNOT_BE_NULL)
    private String id;
    @NotBlank(message = CANNOT_BE_NULL)
    private String pw;
}
