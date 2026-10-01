package org.mini_lab.notificationservice.receive_notification_request.service;

public interface NotificationSender {
    void send(String repcipient, String content);
}
