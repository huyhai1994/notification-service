package org.mini_lab.notificationservice.receive_notification_request.controller;


import org.junit.jupiter.api.Test;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationRequest;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationResponse;
import org.mini_lab.notificationservice.receive_notification_request.dto.NotificationType;
import org.mini_lab.notificationservice.receive_notification_request.exception.NotificationTypeNotSupportedException;
import org.mini_lab.notificationservice.receive_notification_request.service.NotificationService;
import org.mini_lab.notificationservice.shared.constant.Constant;
import org.mini_lab.notificationservice.shared.controller_advice.GlobalControllerAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import(GlobalControllerAdvice.class)
class NotificationControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void process_whenRequestIsValid_thenReturnNotificationResponse()
            throws Exception {

        UUID eventId = UUID.randomUUID();

        NotificationRequest request = new NotificationRequest(
                eventId,
                NotificationType.WELCOME_EMAIL,
                "user@example.com",
                "John"
        );

        NotificationResponse response =
                new NotificationResponse(eventId);

        when(notificationService.process(request))
                .thenReturn(response);

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.eventId")
                        .value(eventId.toString()));

        verify(notificationService).process(request);
    }

    @Test
    void process_whenEventIdIsMissing_thenReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                  "notificationType": "WELCOME_EMAIL",
                  "emailAddress": "user@example.com",
                  "username": "John"
                }
                """;

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code")
                        .value("EVENT_ID_MISSING"))
                .andExpect(jsonPath("$.error.message")
                        .value("Event ID must not be null"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void process_whenNotificationTypeIsMissing_thenReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                  "eventId": "%s",
                  "emailAddress": "user@example.com",
                  "username": "John"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code")
                        .value("NOTIFICATION_TYPE_MISSING"))
                .andExpect(jsonPath("$.error.message")
                        .value("Notification type must not be null"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void process_whenNotificationTypeIsInvalid_thenReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                  "eventId": "%s",
                  "notificationType": "INVALID_TYPE",
                  "emailAddress": "user@example.com",
                  "username": "John"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code")
                        .value("NOTIFICATION_TYPE_INVALID"))
                .andExpect(jsonPath("$.error.message")
                        .value("Invalid notification type"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void process_whenNotificationTypeIsNotSupported_thenReturnBadRequest()
            throws Exception {

        NotificationRequest request = validRequest();

        NotificationTypeNotSupportedException exception =
                mock(NotificationTypeNotSupportedException.class);

        when(notificationService.process(request))
                .thenThrow(exception);

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code")
                        .value("NOTIFICATION_TYPE_NOT_SUPPORTED"))
                .andExpect(jsonPath("$.error.message")
                        .value("Notification type is not supported"));

        verify(notificationService).process(request);
    }

    @Test
    void process_whenEmailAddressIsMissing_thenReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                  "eventId": "%s",
                  "notificationType": "WELCOME_EMAIL",
                  "username": "John"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code")
                        .value("EMAIL_ADDRESS_MISSING"))
                .andExpect(jsonPath("$.error.message")
                        .value("Email address must not be blank"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void process_whenEmailAddressIsBlank_thenReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                  "eventId": "%s",
                  "notificationType": "WELCOME_EMAIL",
                  "emailAddress": "   ",
                  "username": "John"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code")
                        .value("EMAIL_ADDRESS_MISSING"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void process_whenEmailAddressIsInvalid_thenReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                  "eventId": "%s",
                  "notificationType": "WELCOME_EMAIL",
                  "emailAddress": "invalid-email",
                  "username": "John"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code")
                        .value("EMAIL_ADDRESS_INVALID"))
                .andExpect(jsonPath("$.error.message")
                        .value("Email address is invalid"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void process_whenUsernameIsMissing_thenReturnBadRequest()
            throws Exception {

        String requestBody = """
                {
                  "eventId": "%s",
                  "notificationType": "WELCOME_EMAIL",
                  "emailAddress": "user@example.com"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code")
                        .value("USERNAME_MISSING"))
                .andExpect(jsonPath("$.error.message")
                        .value("Username must not be blank"));

        verifyNoInteractions(notificationService);
    }

    @Test
    void process_whenRequestBodyIsMalformed_thenReturnBadRequest()
            throws Exception {

        String malformedJson = """
                {
                  "eventId": "invalid-json"
                """;

        mockMvc.perform(post(Constant.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code")
                        .value("REQUEST_BODY_INVALID"))
                .andExpect(jsonPath("$.error.message")
                        .value("Request body is invalid"));

        verifyNoInteractions(notificationService);
    }


    private NotificationRequest validRequest() {
        return new NotificationRequest(
                UUID.randomUUID(),
                NotificationType.WELCOME_EMAIL,
                "user@example.com",
                "John"
        );
    }
}