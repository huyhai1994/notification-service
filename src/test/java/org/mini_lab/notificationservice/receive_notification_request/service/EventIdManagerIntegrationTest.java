package org.mini_lab.notificationservice.receive_notification_request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mini_lab.notificationservice.support.AbstractIntegrationTest;
import org.mini_lab.notificationservice.support.concurency.RaceConditionSimulator;
import org.mini_lab.notificationservice.support.concurency.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EventIdManagerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    EventIdManager eventIdManager;

    @Value("${app.redis.key}")
    private String key;

    private final String TEST_EVENT_ID = "test-event-id";

    @AfterEach
    void cleanUp() {
        stringRedisTemplate.delete(key);
    }

    @BeforeEach
    void setUp() {
        assertThat(stringRedisTemplate.opsForValue().get(key)).isNull();
    }

    @Test
    void setNx_whenMultipleThreadsClaim_onlyOneSuccess() {
        final int THREAD_COUNTS = 10;
        List<TaskResult<Boolean>> isKeySet;
        try (RaceConditionSimulator raceConditionSimulator = RaceConditionSimulator.getRaceConditionSimulator(THREAD_COUNTS)) {
            isKeySet = raceConditionSimulator.execute(
                    () -> eventIdManager.persistEventId(TEST_EVENT_ID)
            );
        }

        Map<Boolean, List<TaskResult<Boolean>>> partitioned =
                isKeySet
                        .stream()
                        .collect(
                                Collectors.partitioningBy(r -> r.result() == Boolean.TRUE)
                        );

        Awaitility
                .await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(partitioned.get(Boolean.TRUE).stream().count()).isEqualTo(1L);
                    assertThat(partitioned.get(Boolean.FALSE).stream().count()).isEqualTo(9L);
                });
    }

}