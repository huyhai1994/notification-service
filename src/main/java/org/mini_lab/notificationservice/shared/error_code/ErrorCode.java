package org.mini_lab.notificationservice.shared.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    NOTIFICATION_TYPE_NOT_SUPPORTED("Notification type is not supported"),
    NOTIFICATION_TYPE_NON_NULL("NotificationType must not be null");

    private final String defaultMessage;
}
