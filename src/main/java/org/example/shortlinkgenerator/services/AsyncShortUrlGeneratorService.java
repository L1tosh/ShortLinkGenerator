package org.example.shortlinkgenerator.services;

import lombok.AllArgsConstructor;
import org.example.shortlinkgenerator.reposotories.ShortUrlRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AsyncShortUrlGeneratorService {

    private RedisTemplate<String, String> redisTemplate;
    private final ShortUrlRepository shortUrlRepository;

    private static final String REDIS_SHORT_URL_SET_KEY = "short_url_set";
    private static final int COUNT_OF_URL_GENERATE = 50;

    @Async
    public void generateAndSaveShortUrls() {
        for (int i = 0; i < COUNT_OF_URL_GENERATE; i++) {
            String newShortUrl = generateUniqueShortUrl();
            saveUrl(REDIS_SHORT_URL_SET_KEY, newShortUrl);
        }
    }

    private String generateUniqueShortUrl() {
        String url;
        do {
            url = generateShortUrl();
        } while (shortUrlRepository.findByShortUrl(url).isPresent());
        return url;
    }

    private String generateShortUrl() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void saveUrl(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }
}
