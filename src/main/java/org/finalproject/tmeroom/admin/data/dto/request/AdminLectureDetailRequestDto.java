package org.finalproject.tmeroom.admin.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLectureDetailRequestDto {

    @NotBlank(message = CANNOT_BE_NULL)
    private String lectureCode;
}
