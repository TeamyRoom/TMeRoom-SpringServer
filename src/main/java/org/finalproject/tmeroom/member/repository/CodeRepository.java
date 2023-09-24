package org.finalproject.tmeroom.member.repository;

import java.util.Optional;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-19
 * 코드로 아이디를 저장하는 저장소
 */
public interface CodeRepository {

    void save(String Code, String memberId);

    Optional<String> findMemberIdByCode(String Code);

    void deleteByCode(String Code);

}
