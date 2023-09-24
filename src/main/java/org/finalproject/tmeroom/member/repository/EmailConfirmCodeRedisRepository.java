package org.finalproject.tmeroom.member.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

/**
 * 작성자: 김태민
 * 작성 일자: 2023-09-19
 * 이메일 인증 코드로 아이디를 Redis에 저장하는 저장소
 */
@Slf4j
@Repository
public class EmailConfirmCodeRedisRepository extends AbstractCodeRedisRepository
        implements EmailConfirmCodeRepository {

    private static final Duration ROW_DURATION = Duration.ofDays(1);
    private static final String REDIS_PREFIX = "EMAIL_CODE:";

    public EmailConfirmCodeRedisRepository(@Autowired RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate, ROW_DURATION, REDIS_PREFIX);
    }

}
