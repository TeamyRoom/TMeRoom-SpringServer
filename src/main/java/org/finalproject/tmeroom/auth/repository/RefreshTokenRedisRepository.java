package org.finalproject.tmeroom.auth.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-11-01
 * 아이디로 리프레시 토큰을 Redis에 저장하는 저장소 구현체
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository implements RefreshTokenRepository{

    private final RedisTemplate<String, String> redisTemplate;

    private final Duration ROW_DURATION = Duration.ofDays(7);
    private final String REDIS_PREFIX = "REFRESH_TOKEN:";

    private String makeKeyFrom(String memberId) {
        return REDIS_PREFIX + memberId;
    }

    @Override
    public void save(String memberId, String refreshToken) {
        String key = makeKeyFrom(memberId);
        redisTemplate.opsForValue().set(key, refreshToken, ROW_DURATION);
    }

    @Override
    public Optional<String> findRefreshTokenByMemberId(String memberId) {
        String key = makeKeyFrom(memberId);
        String refreshToken = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(refreshToken);
    }

    @Override
    public void deleteByMemberId(String memberId) {
        String key = makeKeyFrom(memberId);
        redisTemplate.delete(key);
    }
}
