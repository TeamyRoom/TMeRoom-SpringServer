package org.finalproject.tmeroom.admin.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.finalproject.tmeroom.admin.constant.MemberSearchType;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
@Builder
public class AdminMemberSearchRequestDto {

    private String keyword;
    private MemberSearchType searchType;
    private Pageable pageable;
}
