package org.mini_lab.notificationservice.receive_notification_request.component;

import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationType;
import org.mini_lab.notificationservice.receive_notification_request.exception.NotificationTypeNotSupportedException;
import org.mini_lab.notificationservice.shared.error_code.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationContentFormatterFactory {

    private final Map<NotificationType, NotificationContentFormatter> formatters;

    public NotificationContentFormatterFactory(
            List<NotificationContentFormatter> formatterList
    ) {
        this.formatters = formatterList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        NotificationContentFormatter::supportedType,
                        Function.identity()
                ));
    }

    public NotificationContentFormatter get(NotificationType type) {
        Objects.requireNonNull(
                type,
                ErrorCode.NOTIFICATION_TYPE_NON_NULL.getDefaultMessage()
        );
        return formatters.get(type);
    }
}