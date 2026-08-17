package org.mini_lab.notificationservice.receive_notification_request.component;

import org.junit.jupiter.api.Test;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationType;
import org.mini_lab.notificationservice.shared.error_code.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class NotificationContentFormatterFactoryIntegrationTest {

    @Autowired
    private NotificationContentFormatterFactory formatterFactory;

    @Autowired
    private WelcomeNotificationFormatter welcomeFormatter;

    @Test
    void get_whenTypeIsWelcomeEmail_thenReturnRegisteredBean() {
        NotificationContentFormatter actual =
                formatterFactory.get(NotificationType.WELCOME_EMAIL);

        assertThat(actual).isSameAs(welcomeFormatter);
    }

    @Test
    void get_whenTypeIsNull_thenThrowNullPointerException() {
        assertThatThrownBy(() -> formatterFactory.get(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(ErrorCode.NOTIFICATION_TYPE_NON_NULL.getDefaultMessage());
    }
}