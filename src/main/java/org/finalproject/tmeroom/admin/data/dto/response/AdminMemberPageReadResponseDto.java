package org.finalproject.tmeroom.admin.data.dto.response;

import lombok.Data;
import org.finalproject.tmeroom.member.data.entity.Member;
import org.springframework.data.domain.Page;

@Data
public class AdminMemberPageReadResponseDto {

    private Page<AdminMemberDetailReadResponseDto> members;

    public static AdminMemberPageReadResponseDto of(Page<Member> members) {
        AdminMemberPageReadResponseDto responseDto = new AdminMemberPageReadResponseDto();
        responseDto.members = members.map(AdminMemberDetailReadResponseDto::from);
        return responseDto;
    }
}
