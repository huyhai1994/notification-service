package org.mini_lab.notificationservice.support;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class AbstractIntegrationTest {
    @Container
    protected static final RedisContainer REDIS_CONTAINER = new RedisContainer(DockerImageName.parse("redis:6.2.6"));


    @Test
    void test() {
        assertThat(REDIS_CONTAINER.isRunning()).isTrue();
    }

}
