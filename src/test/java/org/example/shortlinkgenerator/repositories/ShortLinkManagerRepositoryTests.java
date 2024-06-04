package org.example.shortlinkgenerator.repositories;

import org.assertj.core.api.Assertions;
import org.example.shortlinkgenerator.models.ShortLinkManager;
import org.example.shortlinkgenerator.reposotories.ShortLinkManagerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class ShortLinkManagerRepositoryTests {
    @Autowired
    private ShortLinkManagerRepository shortLinkManagerRepository;

    private ShortLinkManager shortLinkManager;

    @BeforeEach
    public void buildShortUrl() {
        shortLinkManager = ShortLinkManager.builder()
                .shortUrl("a20947c3")
                .createdAt(LocalDateTime.now())
                .url("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146a").build();
    }

    @Test
    @DisplayName("saveShortUrl create new short url and return id")
    public void saveShortUrl_RequestIsValid_ReturnsIdSavedShortUrl() {
        ShortLinkManager savedShortLinkManager = shortLinkManagerRepository.save(shortLinkManager);

        Assertions.assertThat(savedShortLinkManager).isNotNull();
        Assertions.assertThat(savedShortLinkManager.getId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("saveShortUrl create new short url with not unique shortUrl")
    public void saveShortUrl_SaveWithNotUrl_ThrowViolationException() {
        ShortLinkManager notUniqueShortLinkManager = ShortLinkManager.builder()
                .shortUrl("a20947c3")
                .createdAt(LocalDateTime.now())
                .url("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146b").build();

        shortLinkManagerRepository.save(shortLinkManager);

        Assertions.assertThatThrownBy(() ->
            shortLinkManagerRepository.save(notUniqueShortLinkManager)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("saveShortUrl create new short url with not unique url")
    public void saveShortUrl_SaveWithNotUniqueUrl_ThrowViolationException() {
        ShortLinkManager notUniqueShortLinkManager = ShortLinkManager.builder()
                .shortUrl("a20947c4")
                .createdAt(LocalDateTime.now())
                .url("a591a6d40bf420404a011733cfb7b190d62c65bf0bcda32b57b277d9ad9f146a").build();

        shortLinkManagerRepository.save(shortLinkManager);

        Assertions.assertThatThrownBy(() ->
                shortLinkManagerRepository.save(notUniqueShortLinkManager)
        ).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("findByShortUrl returns present Optional of ShortLinkManager")
    public void findByShortUrl_RequestWithExistShortUrl_ReturnsPresentOptional() {
        shortLinkManagerRepository.save(shortLinkManager);

        Optional<ShortLinkManager> findingShortUrl = shortLinkManagerRepository.findByShortUrl(shortLinkManager.getShortUrl());

        Assertions.assertThat(findingShortUrl).isNotEmpty();
        Assertions.assertThat(findingShortUrl.get()).isEqualTo(shortLinkManager);
    }

    @Test
    @DisplayName("findByShortUrl returns empty Optional of ShortLinkManager")
    public void findByUrl_RequestWithExistShortUrl_ReturnsPresentOptional() {
        shortLinkManagerRepository.save(shortLinkManager);

        Optional<ShortLinkManager> findingShortUrl = shortLinkManagerRepository.findByUrl(shortLinkManager.getUrl());

        Assertions.assertThat(findingShortUrl).isNotEmpty();
        Assertions.assertThat(findingShortUrl.get()).isEqualTo(shortLinkManager);
    }

    @Test
    @DisplayName("findByShortUrl delete ShortLinkManager")
    public void delete_RequestWithExistShortUrl_DeleteShortUrlManager() {
        shortLinkManagerRepository.save(shortLinkManager);

        shortLinkManagerRepository.delete(shortLinkManager);

        Assertions.assertThat(shortLinkManagerRepository.findByUrl(shortLinkManager.getUrl())).isEmpty();
    }

    @Test
    @DisplayName("findByShortUrl delete by url ShortLinkManager")
    public void deleteByUrl_RequestWithExistShortUrl_DeleteShortUrlManager() {
        shortLinkManagerRepository.save(shortLinkManager);

        shortLinkManagerRepository.deleteByUrl(shortLinkManager.getUrl());

        Assertions.assertThat(shortLinkManagerRepository.findByUrl(shortLinkManager.getUrl())).isEmpty();
    }
}

