package org.mini_lab.notificationservice.receive_notification_request.component;

import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationType;

public interface NotificationContentFormatter {
    String format(NotificationRequest request);
    NotificationType supportedType();
}
