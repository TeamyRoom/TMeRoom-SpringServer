package org.finalproject.tmeroom.member.data.dto.request;

import lombok.Data;

@Data
public class MemberSendResetCodeRequestDto {

    private String memberId;
    private String email;
}
