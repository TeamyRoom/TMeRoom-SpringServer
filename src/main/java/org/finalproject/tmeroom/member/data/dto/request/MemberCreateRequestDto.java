package org.finalproject.tmeroom.member.data.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.*;

@Data
public class MemberCreateRequestDto {

    @NotBlank(message = CANNOT_BE_NULL)
    @Pattern(regexp = "/^[a-z0-9_]{4,15}$/", message = UNMATCHED_USERNAME)
    private String memberId;
    @NotBlank(message = CANNOT_BE_NULL)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,100}", message = UNMATCHED_PASSWORD)
    private String password;
    @NotBlank(message = CANNOT_BE_NULL)
    @Max(value = 20, message = UNMATCHED_NICKNAME)
    private String nickname;
    @NotBlank(message = CANNOT_BE_NULL)
    @Email(message = UNMATCHED_EMAIL)
    private String email;

    public Member toEntity(PasswordEncoder encoder) {
        return Member.builder()
                .id(memberId)
                .pw(password)
                .nickname(nickname)
                .email(email)
                .encoder(encoder)
                .build();
    }

}
