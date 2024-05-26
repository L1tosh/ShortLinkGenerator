package org.example.shortlinkgenerator.services;

import lombok.AllArgsConstructor;
import org.example.shortlinkgenerator.reposotories.ShortUrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
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
        Set<String> newShortUrls = generateLimitedShortUrl(COUNT_OF_URL_GENERATE);
        saveSet(REDIS_SHORT_URL_SET_KEY, newShortUrls);
    }

    private Set<String> generateLimitedShortUrl(int limit) {
        Set<String> setOfUniqShortUrl = new HashSet<>();
        for (int i = 0; i < limit; i++) {
            setOfUniqShortUrl.add(generateShortUrl());
        }
        return setOfUniqShortUrl;
    }

    private String generateShortUrl() {
        String url = generateUUID();

        while (shortUrlRepository.findByShortUrl(url).isPresent())
            url = generateUUID();

        return url;
    }

    private String generateUUID() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private void saveSet(String key, Set<String> values) {
        redisTemplate.opsForSet().add(key, values.toArray(new String[0]));
    }
}
