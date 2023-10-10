package org.finalproject.tmeroom.admin.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.finalproject.tmeroom.admin.constant.LectureSearchType;
import org.springframework.data.domain.Pageable;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLectureSearchRequestDto {

    private String keyword;
    @NotBlank(message = CANNOT_BE_NULL)
    private LectureSearchType searchType;
    @NotBlank(message = CANNOT_BE_NULL)
    private Pageable pageable;
}
