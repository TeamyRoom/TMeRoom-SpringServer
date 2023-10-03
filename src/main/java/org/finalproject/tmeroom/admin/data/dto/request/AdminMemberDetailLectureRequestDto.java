package org.finalproject.tmeroom.admin.data.dto.request;

import lombok.Data;
import org.finalproject.tmeroom.admin.constant.MemberRoleSearchType;
import org.springframework.data.domain.Pageable;

@Data
public class AdminMemberDetailLectureRequestDto {

    private String memberId;
    private MemberRoleSearchType searchType;
    private Pageable pageable;
}
