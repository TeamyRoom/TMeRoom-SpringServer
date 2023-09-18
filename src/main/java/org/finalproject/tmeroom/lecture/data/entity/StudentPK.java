package org.finalproject.tmeroom.lecture.data.entity;

import java.io.Serializable;

/**
 * 작성자: 김종민
 * 작성일자: 2023-09-11
 * 복합키 생성을 위한 클래스 작성
 */
public class StudentPK implements Serializable {
    private String studentId;

    private String lectureCode;
}
