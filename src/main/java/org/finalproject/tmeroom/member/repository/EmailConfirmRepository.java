package org.finalproject.tmeroom.member.repository;

import java.util.Optional;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-19
 * 이메일 인증 코드를 저장하는 로직
 */
public interface EmailConfirmRepository {

    void save(String confirmCode, String memberId);

    Optional<String> findMemberIdByConfirmCode(String confirmCode);

    void deleteByConfirmCode(String confirmCode);

}
