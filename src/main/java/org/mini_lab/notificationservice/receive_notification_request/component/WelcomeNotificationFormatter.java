package org.mini_lab.notificationservice.receive_notification_request.component;

import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class WelcomeNotificationFormatter implements NotificationContentFormatter {
    @Override
    public String format(NotificationRequest request) {
        return String.format("Welcome %s to our service", request.username());
    }
}
