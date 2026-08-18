package org.mini_lab.notificationservice.receive_notification_request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationResponse;
import org.mini_lab.notificationservice.receive_notification_request.service.NotificationService;
import org.mini_lab.notificationservice.shared.constant.Constant;
import org.mini_lab.notificationservice.shared.response.ApiResponse;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(Constant.BASE_URL)
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> process(@RequestBody @Valid NotificationRequest request) {
        try (MDC.MDCCloseable ignored =
                     MDC.putCloseable("eventId", request.eventId().toString())) {
            NotificationResponse response = notificationService.process(request);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(response));
        }
    }

}
