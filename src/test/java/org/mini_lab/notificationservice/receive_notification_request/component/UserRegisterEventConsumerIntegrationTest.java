package org.mini_lab.notificationservice.receive_notification_request.component;

import org.junit.jupiter.api.Test;
import org.mini_lab.notificationservice.support.AbstractIntegrationTest;
import org.mini_lab.notificationservice.support.MockNotificationRequest;
import org.mini_lab.notificationservice.support.json.ObjectMapperConfig;
import org.mini_lab.notificationservice.support.kafka.KafkaProducerTestConfiguration;
import org.mini_lab.notificationservice.support.kafka.KafkaTestConfiguration;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Import({
        KafkaProducerTestConfiguration.class,
        ObjectMapperConfig.class,
        KafkaTestConfiguration.class,
}
)
class UserRegisterEventConsumerIntegrationTest extends AbstractIntegrationTest {

    @Value("${app.kafka.topic}")
    public String TOPIC;

    public static final String KEY = "user-1";

    @Autowired
    ObjectMapper objectMapperTest;

    @MockitoSpyBean
    UserRegisterEventConsumer userRegisterEventConsumer;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Test
    void send_whenEventSentSuccess_thenVerifyTopicMetadata() throws ExecutionException, InterruptedException, TimeoutException {
        final String PAYLOAD;
        try {
            PAYLOAD = objectMapperTest.writeValueAsString(MockNotificationRequest.getValidNotificationRequest());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(
                        TOPIC,
                        KEY,
                        PAYLOAD
                );

        SendResult<String, String> result =
                future.get(5, TimeUnit.SECONDS);

        assertThat(result).isNotNull();

        assertThat(
                result.getRecordMetadata().topic()
        ).isEqualTo(TOPIC);

        Awaitility.await()
                .atMost(100, TimeUnit.SECONDS).untilAsserted(
                        () -> {
                            ArgumentCaptor<String> messageCaptor =
                                    ArgumentCaptor.forClass(String.class);
                            verify(userRegisterEventConsumer, times(1)).consume(messageCaptor.capture());
                            assertThat(messageCaptor.getValue()).isEqualTo(PAYLOAD);
                            assertThat(userRegisterEventConsumer.mapFrom(messageCaptor.getValue()));
                        });

    }
}