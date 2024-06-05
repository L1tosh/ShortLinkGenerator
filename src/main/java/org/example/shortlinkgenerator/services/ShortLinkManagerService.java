package org.example.shortlinkgenerator.services;

import lombok.AllArgsConstructor;
import org.example.shortlinkgenerator.exceptions.ShortUrlCreateException;
import org.example.shortlinkgenerator.exceptions.ShortUrlNotFoundException;
import org.example.shortlinkgenerator.models.ShortLinkManager;
import org.example.shortlinkgenerator.reposotories.ShortLinkManagerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ShortLinkManagerService {

    private final ShortLinkManagerRepository shortLinkManagerRepository;
    private final AsyncShortUrlGeneratorService asyncShortUrlGeneratorService;

    @Transactional
    public String generateAndSaveShortUrl(String url) {
        Optional<ShortLinkManager> optionalShortUrl = shortLinkManagerRepository.findByUrl(url);

        if (optionalShortUrl.isPresent())
            return optionalShortUrl.get().getShortUrl();

        String uniqueShortUrl;

        try {
            uniqueShortUrl = asyncShortUrlGeneratorService.getUniqueShortUrlFromRedis();
        } catch (RuntimeException e) {
            throw new ShortUrlCreateException();
        }

        shortLinkManagerRepository.save(buildShortLinkManager(url, uniqueShortUrl));

        return uniqueShortUrl;
    }

    @Transactional
    public void deleteShortLinkManager(String url) {
        if (shortLinkManagerRepository.existsByUrl(url)) {
            shortLinkManagerRepository.deleteByUrl(url);
        } else {
            throw new ShortUrlNotFoundException();
        }
    }

    public Optional<ShortLinkManager> getUrlByShortUrl(String url) {
        return shortLinkManagerRepository.findByShortUrl(url);
    }

    private ShortLinkManager buildShortLinkManager(String url, String uniqueShortUrl) {
        return ShortLinkManager.builder()
                .shortUrl(uniqueShortUrl)
                .url(url)
                .createdAt(LocalDateTime.now()).build();
    }
}
