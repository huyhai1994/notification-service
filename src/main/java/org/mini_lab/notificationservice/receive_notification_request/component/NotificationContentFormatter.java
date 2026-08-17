package org.mini_lab.notificationservice.receive_notification_request.component;

import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;

public interface NotificationContentFormatter {
    String format(NotificationRequest request);
}
