package org.mini_lab.notificationservice.receive_notification_request.service;

import lombok.RequiredArgsConstructor;
import org.mini_lab.notificationservice.receive_notification_request.component.NotificationContentFormatter;
import org.mini_lab.notificationservice.receive_notification_request.component.NotificationContentFormatterFactory;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationSender notificationSender;
    private final NotificationContentFormatterFactory notificationContentFormatterFactory;
    private final EventIdManager eventIdManager;

    public NotificationResponse process(NotificationRequest request) {
        if (!Boolean.TRUE.equals(eventIdManager.persistEventId(String.valueOf(request.eventId())))) {
            return new NotificationResponse(request.eventId());
        }
        NotificationContentFormatter notificationContentFormatter =
                notificationContentFormatterFactory.get(request.notificationType());

        String content = notificationContentFormatter.format(request);

        notificationSender.send(request.emailAddress(), content);
        return new NotificationResponse(request.eventId());

    }


}
