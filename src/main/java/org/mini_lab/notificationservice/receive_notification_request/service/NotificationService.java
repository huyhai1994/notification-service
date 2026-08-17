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

    public NotificationResponse process(NotificationRequest notificationRequest) {

        NotificationContentFormatter notificationContentFormatter =
                notificationContentFormatterFactory.get(notificationRequest.notificationType());

        String content = notificationContentFormatter.format(notificationRequest);

        notificationSender.send(notificationRequest.emailAddress(), content);

        return new NotificationResponse(notificationRequest.eventId());
    }


}
