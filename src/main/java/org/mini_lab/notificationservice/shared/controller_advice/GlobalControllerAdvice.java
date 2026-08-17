package org.mini_lab.notificationservice.shared.controller_advice;

import lombok.extern.slf4j.Slf4j;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationType;
import org.mini_lab.notificationservice.receive_notification_request.exception.NotificationTypeNotSupportedException;
import org.mini_lab.notificationservice.shared.error_code.ErrorCode;
import org.mini_lab.notificationservice.shared.response.ApiError;
import org.mini_lab.notificationservice.shared.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.InvalidFormatException;

@Slf4j
@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(NotificationTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>>
    handleNotificationTypeNotSupported(
            NotificationTypeNotSupportedException exception
    ) {
        log.warn(
                "Notification type is not supported: {}",
                exception.getMessage()
        );

        return buildResponse(
                ErrorCode.NOTIFICATION_TYPE_NOT_SUPPORTED
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        ErrorCode errorCode = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::resolveValidationErrorCode)
                .findFirst()
                .orElse(ErrorCode.REQUEST_BODY_INVALID);

        return buildResponse(errorCode);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(
            HttpMessageNotReadableException exception
    ) {
        InvalidFormatException invalidFormatException =
                findCause(exception, InvalidFormatException.class);

        if (isInvalidNotificationType(invalidFormatException)) {
            return buildResponse(
                    ErrorCode.NOTIFICATION_TYPE_INVALID
            );
        }

        return buildResponse(
                ErrorCode.REQUEST_BODY_INVALID
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
            Exception exception
    ) {
        log.error("Unexpected exception", exception);

        return buildResponse(
                ErrorCode.INTERNAL_SERVER_ERROR
        );
    }

    private ErrorCode resolveValidationErrorCode(
            FieldError fieldError
    ) {
        return switch (fieldError.getField()) {
            case "eventId" -> ErrorCode.EVENT_ID_MISSING;

            case "notificationType" -> ErrorCode.NOTIFICATION_TYPE_MISSING;

            case "emailAddress" -> {
                if ("Email".equals(fieldError.getCode())) {
                    yield ErrorCode.EMAIL_ADDRESS_INVALID;
                }

                yield ErrorCode.EMAIL_ADDRESS_MISSING;
            }

            case "username" -> ErrorCode.USERNAME_MISSING;

            default -> ErrorCode.REQUEST_BODY_INVALID;
        };
    }

    private boolean isInvalidNotificationType(
            InvalidFormatException exception
    ) {
        return exception != null
                && NotificationType.class.equals(
                exception.getTargetType()
        );
    }

    private ResponseEntity<ApiResponse<Void>> buildResponse(
            ErrorCode errorCode
    ) {
        ApiError apiError = new ApiError(
                errorCode,
                errorCode.getDefaultMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(apiError));
    }

    private <T extends Throwable> T findCause(
            Throwable throwable,
            Class<T> expectedType
    ) {
        Throwable current = throwable;

        while (current != null) {
            if (expectedType.isInstance(current)) {
                return expectedType.cast(current);
            }

            current = current.getCause();
        }

        return null;
    }
}