package org.finalproject.tmeroom.admin.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.finalproject.tmeroom.admin.constant.LectureSearchType;
import org.springframework.data.domain.Pageable;

@Data
@AllArgsConstructor
@Builder
public class AdminLectureSearchRequestDto {

    private String keyword;
    private LectureSearchType searchType;
    private Pageable pageable;
}
