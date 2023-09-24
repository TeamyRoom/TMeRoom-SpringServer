package org.finalproject.tmeroom.member.data.dto.request;

import lombok.Data;

@Data
public class PasswordResetRequestDto {

    private String resetCode;
    private String newPassword;
}
