package org.mini_lab.notificationservice.support;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public abstract class AbstractIntegrationTest {

    protected static final RedisContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER =
                new RedisContainer(
                        DockerImageName.parse("redis:6.2.6")
                )
                        .withExposedPorts(6379);

        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    static void registerRedisProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.data.redis.host",
                REDIS_CONTAINER::getHost
        );

        registry.add(
                "spring.data.redis.port",
                () -> REDIS_CONTAINER.getMappedPort(6379)
        );
    }
}