package com.flowforge.api.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class RateLimitService {

    private static final DefaultRedisScript<Long> RATE_LIMIT_SCRIPT =
            new DefaultRedisScript<>("""
                    local current = redis.call('INCR', KEYS[1])

                    if current == 1 then
                        redis.call('EXPIRE', KEYS[1], ARGV[1])
                    end

                    return current
                    """, Long.class);

    private final StringRedisTemplate redisTemplate;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(
            String key,
            int limit,
            Duration window
    ) {
        Long count = redisTemplate.execute(
                RATE_LIMIT_SCRIPT,
                List.of(key),
                String.valueOf(window.toSeconds())
        );

        return count != null && count <= limit;
    }
}
