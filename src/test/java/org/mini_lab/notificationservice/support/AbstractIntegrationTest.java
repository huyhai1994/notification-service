package org.mini_lab.notificationservice.support;

import com.redis.testcontainers.RedisContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractIntegrationTest {

    protected static final RedisContainer REDIS_CONTAINER;
    protected static final KafkaContainer KAFKA_CONTAINER;

    static {
        REDIS_CONTAINER =
                new RedisContainer(
                        DockerImageName.parse("redis:6.2.6")
                )
                        .withExposedPorts(6379);
        KAFKA_CONTAINER = new KafkaContainer(
                DockerImageName.parse("apache/kafka:3.9.2")
        );

        REDIS_CONTAINER.start();
        KAFKA_CONTAINER.start();

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

        registry.add(
                "spring.kafka.bootstrap-servers",
                KAFKA_CONTAINER::getBootstrapServers
        );
    }
}