package org.example.shortlinkgenerator.services;

import org.assertj.core.api.Assertions;
import org.example.shortlinkgenerator.reposotories.ShortLinkManagerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncShortUrlGeneratorServiceTest {

    @Mock
    RedisService redisService;
    @Mock
    ShortLinkManagerRepository shortLinkManagerRepository;
    @InjectMocks
    AsyncShortUrlGeneratorService service;

    @Captor
    private ArgumentCaptor<String> argumentCaptor;

    @Test
    void getUniqueShortUrlFromRedis_RedisHasShortUrl_ReturnsUniqueShortUrl() {
        // given
        final String REDIS_SHORT_URL_SET_KEY = "short_url_set";
        String shortUrl = "unique12";
        // when
        when(redisService.popSetElement(REDIS_SHORT_URL_SET_KEY))
                .thenReturn(shortUrl);
        // then
        Assertions.assertThat(service.getUniqueShortUrlFromRedis()).isEqualTo(shortUrl);
    }

    @Test
    void getUniqueShortUrlFromRedis_RedisHasNotShortUrl_ReturnsUniqueShortUrl() {
        // given
        final String REDIS_SHORT_URL_SET_KEY = "short_url_set";
        String shortUrl = "unique12";
        // when
        when(redisService.popSetElement(REDIS_SHORT_URL_SET_KEY))
                .thenReturn(shortUrl);
        // then
        Assertions.assertThat(service.getUniqueShortUrlFromRedis()).isEqualTo(shortUrl);
    }

    @Test
    void generateShortUrl_ReturnsString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        Method generateShortUrl = AsyncShortUrlGeneratorService.class.getDeclaredMethod("generateShortUrl");
        generateShortUrl.setAccessible(true);
        String result = (String) generateShortUrl.invoke(service);
        // when

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.length()).isEqualTo(8);
    }

    @Test
    void generateAndSaveToRedisShortUrls_SavesCorrectNumberOfUrls() throws InterruptedException, ExecutionException {
        final byte SHORT_URL_LENGTH = 8;
        final int COUNT_OF_URL_GENERATE = 50;

        CompletableFuture<Void> future =
                CompletableFuture.runAsync(() -> service.generateAndSaveToRedisShortUrls());

        future.get();

        verify(redisService, times(COUNT_OF_URL_GENERATE))
                .saveShortUrl(anyString(), argumentCaptor.capture());

        List<String> capturedValues = argumentCaptor.getAllValues();

        for (String url : capturedValues) {
            assert url.length() == SHORT_URL_LENGTH : "The length of the saved link is not 8 characters";
        }
    }

}
