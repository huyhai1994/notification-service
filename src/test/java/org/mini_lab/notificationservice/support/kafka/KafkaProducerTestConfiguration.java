package org.mini_lab.notificationservice.support.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

@TestConfiguration
public class KafkaProducerTestConfiguration {
    @Bean
    public ProducerFactory<String, String> producerFactory(
            KafkaProperties kafkaProperties
    ) {
        Map<String, Object> props =
                kafkaProperties.buildProducerProperties();

        props.put(
                ProducerConfig.MAX_BLOCK_MS_CONFIG,
                1000
        );

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(
            ProducerFactory<String, String> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}
