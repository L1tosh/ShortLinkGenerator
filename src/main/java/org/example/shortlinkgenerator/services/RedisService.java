package org.example.shortlinkgenerator.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public String popSetElement(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    public void saveShortUrl(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }
}
