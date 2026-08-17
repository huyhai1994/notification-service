package org.mini_lab.notificationservice.shared.error_code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EVENT_ID_MISSING(
            HttpStatus.BAD_REQUEST,
            "Event ID must not be null"
    ),

    NOTIFICATION_TYPE_MISSING(
            HttpStatus.BAD_REQUEST,
            "Notification type must not be null"
    ),

    NOTIFICATION_TYPE_INVALID(
            HttpStatus.BAD_REQUEST,
            "Invalid notification type"
    ),

    NOTIFICATION_TYPE_NOT_SUPPORTED(
            HttpStatus.BAD_REQUEST,
            "Notification type is not supported"
    ),

    EMAIL_ADDRESS_MISSING(
            HttpStatus.BAD_REQUEST,
            "Email address must not be blank"
    ),

    EMAIL_ADDRESS_INVALID(
            HttpStatus.BAD_REQUEST,
            "Email address is invalid"
    ),

    USERNAME_MISSING(
            HttpStatus.BAD_REQUEST,
            "Username must not be blank"
    ),

    NOTIFICATION_PROVIDER_TIMEOUT(
            HttpStatus.GATEWAY_TIMEOUT,
            "Notification provider timed out"
    ),

    REQUEST_BODY_INVALID(
            HttpStatus.BAD_REQUEST,
            "Request body is invalid"
    ),

    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred"
    );

    private final HttpStatus httpStatus;
    private final String defaultMessage;
}