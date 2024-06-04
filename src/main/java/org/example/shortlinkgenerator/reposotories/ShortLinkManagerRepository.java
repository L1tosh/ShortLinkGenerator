package org.example.shortlinkgenerator.reposotories;

import org.example.shortlinkgenerator.models.ShortLinkManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortLinkManagerRepository extends JpaRepository<ShortLinkManager, Long> {
    Optional<ShortLinkManager> findByUrl(String url);
    Optional<ShortLinkManager> findByShortUrl(String shortUrl);
    void deleteByUrl(String url);
    boolean existsByUrl(String url);
}
