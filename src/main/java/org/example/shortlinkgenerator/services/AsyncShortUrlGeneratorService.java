package org.example.shortlinkgenerator.services;

import lombok.AllArgsConstructor;
import org.example.shortlinkgenerator.reposotories.ShortLinkManagerRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class AsyncShortUrlGeneratorService {

    private final RedisService redisService;
    private final ShortLinkManagerRepository shortLinkManagerRepository;

    private static final String REDIS_SHORT_URL_SET_KEY = "short_url_set";
    private static final int RETRY_COUNT = 5;
    private static final int RETRY_DELAY_MS = 200;


    private static final int COUNT_OF_URL_GENERATE = 50;
    private static final byte SHORT_URL_LENGTH = 8;

    public String getUniqueShortUrlFromRedis() {
        String uniqueShortUrl = redisService.popSetElement(REDIS_SHORT_URL_SET_KEY);

        if (uniqueShortUrl == null) {
            // Starting an asynchronous link generation process
            generateAndSaveToRedisShortUrls();

            // Repeated attempts to obtain a link with a delay
            for (int i = 0; i < RETRY_COUNT; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                uniqueShortUrl = redisService.popSetElement(REDIS_SHORT_URL_SET_KEY);
                if (uniqueShortUrl != null) {
                    break;
                }
            }

            // If after repeated attempts the link is not found, throw an exception
            if (uniqueShortUrl == null) {
                throw new RuntimeException("Failed to generate unique short URL");
            }
        }

        return uniqueShortUrl;
    }

    @Async
    void generateAndSaveToRedisShortUrls() {
        for (int i = 0; i < COUNT_OF_URL_GENERATE; i++) {
            String newShortUrl = generateUniqueShortUrl();
            redisService.saveShortUrl(REDIS_SHORT_URL_SET_KEY, newShortUrl);
        }
    }

    private String generateUniqueShortUrl() {
        String url;
        do {
            url = generateShortUrl();
        } while (shortLinkManagerRepository.findByShortUrl(url).isPresent());
        return url;
    }

    private String generateShortUrl() {
        return UUID.randomUUID().toString().substring(0, SHORT_URL_LENGTH);
    }
}
