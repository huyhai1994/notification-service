package org.mini_lab.notificationservice.shared.response;

import org.mini_lab.notificationservice.shared.error_code.ErrorCode;

public record ApiError(ErrorCode code, String message) {
}
