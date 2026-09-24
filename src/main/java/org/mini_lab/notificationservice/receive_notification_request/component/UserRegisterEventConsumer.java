package org.mini_lab.notificationservice.receive_notification_request.component;

import lombok.RequiredArgsConstructor;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.receive_notification_request.service.NotificationService;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisterEventConsumer {
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${app.kafka.group-id}"
    )
    public void consume(String message) {
        notificationService.process(mapFrom(message));
    }

    public NotificationRequest mapFrom(String message) {
        return objectMapper.readValue(message, new TypeReference<>() {
        });

    }
}