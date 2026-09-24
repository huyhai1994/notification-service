package org.mini_lab.notificationservice.support.json;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@TestConfiguration
public class ObjectMapperConfig {

    @Bean
    ObjectMapper objectMapperTest(){
        return new ObjectMapper();
    }
}
