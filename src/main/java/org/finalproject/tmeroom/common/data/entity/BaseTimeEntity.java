package org.finalproject.tmeroom.common.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 작성자: 김태민
 * 작성일자: 2023-09-11
 * 모든 엔티티에 작성 일자와 수정 일자를 자동으로 표기하기 위한 엔티티입니다.
 * 엔티티 작성시 extends 키워드로 해당 클래스를 상속해 사용할 수 있습니다.
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
@NoArgsConstructor
public class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
