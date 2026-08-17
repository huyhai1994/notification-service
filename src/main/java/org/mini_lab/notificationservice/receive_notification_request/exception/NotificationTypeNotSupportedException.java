package org.mini_lab.notificationservice.receive_notification_request.exception;

import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationType;
import org.mini_lab.notificationservice.shared.error_code.ErrorCode;

public class NotificationTypeNotSupportedException extends RuntimeException {

    public NotificationTypeNotSupportedException(NotificationType type) {
        super(ErrorCode.NOTIFICATION_TYPE_NOT_SUPPORTED.getDefaultMessage() + type);
    }
}
