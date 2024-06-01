package org.example.shortlinkgenerator.services;

import lombok.AllArgsConstructor;
import org.example.shortlinkgenerator.models.ShortUrl;
import org.example.shortlinkgenerator.reposotories.ShortUrlRepository;
import org.example.shortlinkgenerator.utill.errors.ShortUrlCreateException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ShortLUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final AsyncShortUrlGeneratorService asyncShortUrlGeneratorService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_SHORT_URL_SET_KEY = "short_url_set";
    private static final int RETRY_COUNT = 5;
    private static final int RETRY_DELAY_MS = 200;

    @Transactional
    public String saveShortUrl(String url) {
        Optional<ShortUrl> optionalShortUrl = shortUrlRepository.findByUrl(url);
        if (optionalShortUrl.isPresent())
            return optionalShortUrl.get().getShortUrl();

        String uniqueShortUrl = getUniqueShortUrlFromRedis();

        ShortUrl shortUrl = buildShortUrl(url, uniqueShortUrl);

        shortUrlRepository.save(shortUrl);

        return uniqueShortUrl;
    }   

    private ShortUrl buildShortUrl(String url, String uniqueShortUrl) {
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortUrl(uniqueShortUrl);
        shortUrl.setUrl(url);
        shortUrl.setCreatedAt(LocalDateTime.now());
        return shortUrl;
    }

    @Transactional
    public Boolean deleteShortUrl(String url) {
        shortUrlRepository.deleteByUrl(url);
        return true;
    }

    private String getUniqueShortUrlFromRedis() {
        String uniqueShortUrl = popSetElement(REDIS_SHORT_URL_SET_KEY);

        if (uniqueShortUrl == null) {
            // Starting an asynchronous link generation process
            asyncShortUrlGeneratorService.generateAndSaveShortUrls();

            // Repeated attempts to obtain a link with a delay
            for (int i = 0; i < RETRY_COUNT; i++) {
                try {
                    TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                uniqueShortUrl = popSetElement(REDIS_SHORT_URL_SET_KEY);
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

    public String popSetElement(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    public Optional<ShortUrl> getShortUrl(String url) {
        return shortUrlRepository.findByShortUrl(url);
    }
}
