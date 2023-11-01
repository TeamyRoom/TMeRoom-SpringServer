package org.finalproject.tmeroom.auth.repository;

import java.util.Optional;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-11-01
 * 아이디로 리프레시토큰을 저장하는 저장소 인터페이스
 */
public interface RefreshTokenRepository {

    void save(String memberId, String refreshToken);

    Optional<String> findRefreshTokenByMemberId(String memberId);

    void deleteByMemberId(String memberId);
}
