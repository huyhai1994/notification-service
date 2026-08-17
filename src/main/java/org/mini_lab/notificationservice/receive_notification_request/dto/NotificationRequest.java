package org.mini_lab.notificationservice.receive_notification_request.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record NotificationRequest(
        @NotNull
        UUID eventId,
        @NotNull
        NotificationType notificationType,
        @NotNull
        @Email
        @NotEmpty
        @NotBlank
        String emailAddress,
        @NotNull
        @NotBlank
        @NotEmpty
        String username
) {
}
