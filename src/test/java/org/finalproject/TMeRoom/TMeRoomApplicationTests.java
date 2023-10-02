package org.finalproject.TMeRoom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        properties = {
                "spring.config.additional-location=classpath:config/*/",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
        }
)
@TestPropertySource(properties = {
        "JWT_KEY=LongLongLongLongLongLongLongLongTestJWTKey"
})
@ActiveProfiles("test")
class TMeRoomApplicationTests {

    @Test
    void contextLoads() {
    }

}
