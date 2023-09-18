package org.finalproject.TMeRoom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "JWT_KEY=LongLongLongLongLongLongLongLongTestJWTKey"
})
@ActiveProfiles("test")
class TMeRoomApplicationTests {

    @Test
    void contextLoads() {
    }

}
