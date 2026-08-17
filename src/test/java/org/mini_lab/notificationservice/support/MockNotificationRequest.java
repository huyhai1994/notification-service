package org.mini_lab.notificationservice.support;

import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationType;

import java.util.UUID;

public class MockNotificationRequest {

    public static NotificationRequest getValidNotificationRequest() {
        return NotificationRequest.builder()
                .eventId(UUID.randomUUID())
                .emailAddress(MockEmailAddress.validEmail)
                .username(MockUserBuilder.username)
                .notificationType(NotificationType.WELCOME_EMAIL)
                .build();
    }
}
