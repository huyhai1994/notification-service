package org.mini_lab.notificationservice.receive_notification_request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mini_lab.notificationservice.receive_notification_request.component.NotificationContentFormatter;
import org.mini_lab.notificationservice.receive_notification_request.component.NotificationContentFormatterFactory;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationResponse;
import org.mini_lab.notificationservice.receive_notification_request.exception.NotificationTypeNotSupportedException;
import org.mini_lab.notificationservice.shared.error_code.ErrorCode;
import org.mini_lab.notificationservice.support.MockNotificationRequest;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceMockTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private NotificationSender notificationSender;

    @Mock
    private NotificationContentFormatter notificationContentFormatter;

    @Mock
    private NotificationContentFormatterFactory notificationContentFormatterFactory;

    @Test
    void process_whenProcessSucceeded_thenReturnNotificationResponse() {
        // Arrange
        NotificationRequest request =
                MockNotificationRequest.getValidNotificationRequest();

        String formattedContent = "Welcome email";

        when(notificationContentFormatterFactory.get(
                request.notificationType()
        )).thenReturn(notificationContentFormatter);

        when(notificationContentFormatter.format(request))
                .thenReturn(formattedContent);

        // Act
        NotificationResponse response =
                notificationService.process(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.eventId()).isEqualTo(request.eventId());

        InOrder inOrder = inOrder(
                notificationContentFormatterFactory,
                notificationContentFormatter,
                notificationSender
        );

        inOrder.verify(notificationContentFormatterFactory)
                .get(request.notificationType());

        inOrder.verify(notificationContentFormatter)
                .format(request);

        inOrder.verify(notificationSender)
                .send(request.emailAddress(), formattedContent);

        verifyNoMoreInteractions(
                notificationContentFormatterFactory,
                notificationContentFormatter,
                notificationSender
        );
    }

}