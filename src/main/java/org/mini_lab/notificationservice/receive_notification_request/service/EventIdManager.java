package org.mini_lab.notificationservice.receive_notification_request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EventIdManager {
    private final StringRedisTemplate stringRedisTemplate;
    @Value("${app.redis.key}")
    private String key;

    @Value("${app.redis.ttl}")
    private long value;

    public Boolean persistEventId(String eventId) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, eventId, Duration.ofSeconds(value));
    }

    public String getEventId(String eventId) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public String deleteEventId(String eventId) {
        throw new UnsupportedOperationException();
    }
}
