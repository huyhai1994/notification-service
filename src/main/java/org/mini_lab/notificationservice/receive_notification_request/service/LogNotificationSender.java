package org.mini_lab.notificationservice.receive_notification_request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class LogNotificationSender implements NotificationSender {

    @Override
    public void send(String repcipient, String content) throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        log.info("SEND_NOTIFICATION to repcipient={} with content={}", repcipient, content);
    }
}
