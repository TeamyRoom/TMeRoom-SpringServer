package org.finalproject.tmeroom.auth.data.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginResponseDto {

    private String accessToken;

    @Builder
    public LoginResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
