package org.example.shortlinkgenerator.services;

import org.assertj.core.api.Assertions;
import org.example.shortlinkgenerator.entity.ShortLinkManager;
import org.example.shortlinkgenerator.reposotories.ShortLinkManagerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortLinkManagerServiceTest {
    @Mock
    ShortLinkManagerRepository shortLinkManagerRepository;
    @Mock
    AsyncShortUrlGeneratorService asyncShortUrlGeneratorService;
    @InjectMocks
    ShortLinkManagerService service;

    @Test
    void generateAndSaveShortUrl_WhenUrlWithShortUrlNotExist_ReturnsShortUrl() {
        // given
        final String url = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146a";
        final String shortUrl = "unique12";

        // when
        when(shortLinkManagerRepository.findByUrl(url))
                .thenReturn(Optional.empty());
        when(asyncShortUrlGeneratorService.getUniqueShortUrlFromRedis())
                .thenReturn(shortUrl);

        // then
        Assertions.assertThat(service.generateAndSaveShortUrl(url)).isEqualTo(shortUrl);
    }

    @Test
    void generateAndSaveShortUrl_WhenUrlWithShortUrlExist_ReturnsShortUrl() {
        // given
        final String url = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146a";
        final String shortUrl = "unique12";

        ShortLinkManager build = ShortLinkManager.builder()
                .id(1L)
                .shortUrl(shortUrl)
                .url(url)
                .createdAt(LocalDateTime.now()).build();

        // when
        when(shortLinkManagerRepository.findByUrl(url))
                .thenReturn(Optional.of(build));

        // then
        Assertions.assertThat(service.generateAndSaveShortUrl(url)).isEqualTo(shortUrl);
    }

    @Test
    void getUrlByShortUrl_ShortUrlExist_ReturnsPresentOption() {
        // given
        final String url = "a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146a";
        final String shortUrl = "unique12";

        ShortLinkManager build = ShortLinkManager.builder()
                .id(1L)
                .shortUrl(shortUrl)
                .url(url)
                .createdAt(LocalDateTime.now()).build();

        // when
        when(shortLinkManagerRepository.findByShortUrl(shortUrl))
                .thenReturn(Optional.of(build));
        // then
        Assertions.assertThat(service.getUrlByShortUrl(shortUrl)).isNotEmpty();
        Assertions.assertThat(service.getUrlByShortUrl(shortUrl).get())
                .isEqualTo(build);

    }

    @Test
    void getUrlByShortUrl_ShortUrlNotExist_ReturnsEmptyOption() {
        // given
        final String shortUrl = "unique12";
        // when
        when(shortLinkManagerRepository.findByShortUrl(shortUrl))
                .thenReturn(Optional.empty());
        // then
        Assertions.assertThat(service.getUrlByShortUrl(shortUrl)).isEmpty();
    }
}