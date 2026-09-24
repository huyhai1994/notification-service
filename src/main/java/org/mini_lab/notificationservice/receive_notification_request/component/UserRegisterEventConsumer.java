package org.mini_lab.notificationservice.receive_notification_request.component;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterEventConsumer {

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${app.kafka.group-id}"
    )
    public void consume(String message) {
        log.info("Received message: {}", message);
    }
}