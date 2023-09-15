package org.finalproject.tmeroom.lecture.service;

import org.finalproject.tmeroom.common.exception.ApplicationException;
import org.finalproject.tmeroom.common.exception.ErrorCode;
import org.finalproject.tmeroom.lecture.data.entity.Lecture;
import org.finalproject.tmeroom.member.data.dto.MemberDto;

import java.util.Objects;

public abstract class LectureCommon {
    protected void checkPermission(Lecture lecture, MemberDto memberDTO) {
        if (memberDTO == null || !Objects.equals(lecture.getManager().getId(), memberDTO.getId())) {
            throw new ApplicationException(ErrorCode.INVALID_PERMISSION);
        }
    }
}
