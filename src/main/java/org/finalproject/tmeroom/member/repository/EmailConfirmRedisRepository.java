package org.finalproject.tmeroom.member.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-19
 * 이메일 인증 코드를 Redis에 저장하는 로직
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmailConfirmRedisRepository implements EmailConfirmRepository {

    private final static Duration ITEM_TTL = Duration.ofDays(1);
    private final static String REDIS_PREFIX = "EMAIL_CODE:";
    private final RedisTemplate<String, String> redisTemplate;

    private String getKey(String key) {
        return REDIS_PREFIX + key;
    }

    @Override
    public void save(String confirmCode, String memberId) {
        String key = getKey(confirmCode);
        redisTemplate.opsForValue().set(key, memberId, ITEM_TTL);
    }

    @Override
    public Optional<String> findMemberIdByConfirmCode(String confirmCode) {
        String key = getKey(confirmCode);
        String memberId = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(memberId);
    }

    @Override
    public void deleteByConfirmCode(String confirmCode) {
        String key = getKey(confirmCode);
        redisTemplate.delete(key);
    }
}
