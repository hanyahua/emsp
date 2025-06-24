package com.volvo.emsp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
@SpringBootTest
@Import(TestRedisConfiguration.class)
class EMspApplicationTests {

    @Test
    void contextLoads() {
    }

}
