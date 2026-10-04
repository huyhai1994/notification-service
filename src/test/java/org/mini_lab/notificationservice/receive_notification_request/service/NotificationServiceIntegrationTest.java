package org.mini_lab.notificationservice.receive_notification_request.service;

import org.junit.jupiter.api.Test;
import org.mini_lab.notificationservice.receive_notification_request.component.NotificationContentFormatter;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.support.AbstractIntegrationTest;
import org.mini_lab.notificationservice.support.MockNotificationRequest;
import org.mini_lab.notificationservice.support.concurency.RaceConditionSimulator;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class NotificationServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    NotificationService notificationService;

    @MockitoSpyBean
    NotificationSender notificationSender;

    @MockitoSpyBean
    NotificationContentFormatter notificationContentFormatter;

    @Test
    void process_whenRequestsDuplicated_thenOnlyProcessOne() {

        NotificationRequest request =
                MockNotificationRequest.getValidNotificationRequest();

        try (RaceConditionSimulator raceConditionSimulator =
                     RaceConditionSimulator.getRaceConditionSimulator(2)) {

            raceConditionSimulator.execute(
                    () -> notificationService.process(request)
            );
        }

        ArgumentCaptor<String> contentCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(notificationSender, times(1))
                .send(
                        eq(request.emailAddress()),
                        contentCaptor.capture()
                );

        String capturedContent = contentCaptor.getValue();

        assertThat(capturedContent).isNotNull();
    }
}