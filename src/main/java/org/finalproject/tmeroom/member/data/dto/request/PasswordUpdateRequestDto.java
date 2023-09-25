package org.finalproject.tmeroom.member.data.dto.request;

import lombok.Data;

@Data
public class PasswordUpdateRequestDto {

    private String oldPassword;
    private String newPassword;
}
