package org.finalproject.tmeroom.auth.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;

@Data
@Builder
public class RefreshRequestDto {

    @NotBlank(message = CANNOT_BE_NULL)
    private String refreshToken;
}
