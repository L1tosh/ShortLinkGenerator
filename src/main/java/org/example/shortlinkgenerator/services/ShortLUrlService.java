package org.example.shortlinkgenerator.services;

import lombok.AllArgsConstructor;
import org.example.shortlinkgenerator.models.ShortUrl;
import org.example.shortlinkgenerator.reposotories.ShortUrlRepository;
import org.example.shortlinkgenerator.utill.errors.ShortUrlCreateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ShortLUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public Optional<ShortUrl> getShortUrl(String url) {
        return shortUrlRepository.findByShortUrl(url);
    }

    @Transactional
    public String saveShortUrl(Long postId) {
        if (shortUrlRepository.findByPostId(postId).isPresent())
            throw new ShortUrlCreateException("This post already has a short link");

        String uniqueShortUrl = generateShortUrl();

        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortUrl(uniqueShortUrl);
        shortUrl.setPostId(postId);
        shortUrl.setCreatedAt(LocalDateTime.now());

        shortUrlRepository.save(shortUrl);

        return uniqueShortUrl;
    }

    @Transactional
    public Boolean deleteShortUrl(Long postId) {
        shortUrlRepository.deleteByPostId(postId);
        return true;
    }

    private String generateShortUrl() {
        String url = generateUniqueString();

        while (shortUrlRepository.findByShortUrl(url).isPresent()) {
            url = generateUniqueString();
        }

        return url;
    }

    private String generateUniqueString() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 8);
    }
}
