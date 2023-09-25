package org.finalproject.tmeroom.member.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-24
 * 코드로 값을 Redis에 저장하는 기능의 구현부 캡슐화
 */
@RequiredArgsConstructor
public abstract class AbstractCodeRedisRepository implements CodeRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final Duration ROW_DURATION;
    private final String REDIS_PREFIX;

    private String makeKeyFrom(String code) {
        return REDIS_PREFIX + code;
    }

    @Override
    public void save(String code, String value) {
        String key = makeKeyFrom(code);
        redisTemplate.opsForValue().set(key, value, ROW_DURATION);
    }

    @Override
    public Optional<String> findMemberIdByCode(String code) {
        String key = makeKeyFrom(code);
        String memberId = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(memberId);
    }

    @Override
    public void deleteByCode(String confirmCode) {
        String key = makeKeyFrom(confirmCode);
        redisTemplate.delete(key);
    }
}
