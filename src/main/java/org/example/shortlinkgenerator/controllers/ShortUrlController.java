package org.example.shortlinkgenerator.controllers;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.example.shortlinkgenerator.exceptions.ShortUrlNotFoundException;
import org.example.shortlinkgenerator.entity.ShortLinkManager;
import org.example.shortlinkgenerator.services.ShortLinkManagerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ShortUrlController {
    private final ShortLinkManagerService shortLinkManagerService;

    @PostMapping("/create")
    public ResponseEntity<String> crateShortUrl(@RequestBody @NotEmpty String url) {
        String uniqueShortUrl = shortLinkManagerService.generateAndSaveShortUrl(url);
        return new ResponseEntity<>(uniqueShortUrl, HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getPostByShortUrl(@RequestParam @NotEmpty @Size(min = 8, max = 8) String shortUrl) {
        Optional<ShortLinkManager> searchedShortUrl = shortLinkManagerService.getUrlByShortUrl(shortUrl);

        if (searchedShortUrl.isEmpty())
            throw new ShortUrlNotFoundException();

        return new ResponseEntity<>(searchedShortUrl.get().getUrl(), HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<HttpStatus> deleteShortUrl(@RequestBody @NotEmpty String url) {
        shortLinkManagerService.deleteShortLinkManager(url);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

