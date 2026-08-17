package org.mini_lab.notificationservice.receive_notification_request.component;

import org.junit.jupiter.api.Test;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.support.MockNotificationRequest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class WelcomeNotificationFormatterTest {
    private final WelcomeNotificationFormatter welcomeNotificationFormatter = new WelcomeNotificationFormatter();


    @Test
    void format_whenRequestIsValid_returnWelcomeContent() {
        NotificationRequest notificationRequest = MockNotificationRequest.getValidNotificationRequest();
        String welcomeEmail = welcomeNotificationFormatter.format(notificationRequest);
        assertThat(welcomeEmail).isNotNull();
        assertThat(welcomeEmail).isEqualTo("Welcome " + notificationRequest.username() + " to our service");
    }


}