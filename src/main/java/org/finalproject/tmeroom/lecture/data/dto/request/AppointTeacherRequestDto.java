package org.finalproject.tmeroom.lecture.data.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import static org.finalproject.tmeroom.common.exception.ValidationMessage.CANNOT_BE_NULL;

@Getter
@Setter
public class AppointTeacherRequestDto {

    @NotBlank(message = CANNOT_BE_NULL)
    String teacherId;
}