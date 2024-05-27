package org.example.shortlinkgenerator.reposotories;

import org.example.shortlinkgenerator.models.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByUrl(String url);
    Optional<ShortUrl> findByShortUrl(String shortUrl);
    void deleteByUrl(String url);
}
