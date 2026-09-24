package org.mini_lab.notificationservice.support.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@TestConfiguration
public class KafkaTestConfiguration {

    @Bean
    NewTopic registerEventsTopic() {
        return TopicBuilder
                .name("test.notifications.register-events.v1")
                .partitions(1)
                .replicas(1)
                .build();
    }
}