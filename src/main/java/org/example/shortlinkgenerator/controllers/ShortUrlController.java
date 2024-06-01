package org.example.shortlinkgenerator.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.example.shortlinkgenerator.models.ShortUrl;
import org.example.shortlinkgenerator.services.ShortLUrlService;
import org.example.shortlinkgenerator.utill.ResponseError;
import org.example.shortlinkgenerator.utill.errors.ShortUrlCreateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ShortUrlController {

    private final ShortLUrlService shortLUrlService;

    @PostMapping("/create")
    public ResponseEntity<String> crateShortLink(@RequestBody @NotNull @Valid String url) {
        String uniqueShortUrl = shortLUrlService.saveShortUrl(url);
        return new ResponseEntity<>(uniqueShortUrl, HttpStatus.OK);
    }

    @GetMapping("/get")
    public ResponseEntity<String> getPostByShortUrl(@RequestParam @NotNull String shortUrl) {
        Optional<ShortUrl> searchedShortUrl = shortLUrlService.getShortUrl(shortUrl);

        if (shortUrl.isEmpty())
            throw new RuntimeException("Post does not have a short link");

        System.out.println("success get: " + shortUrl);

        return new ResponseEntity<>(searchedShortUrl.get().getUrl(), HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> deleteShortUrl(@RequestBody @NotNull String url) {
        return new ResponseEntity<>(shortLUrlService.deleteShortUrl(url), HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<ResponseError> handleException(ShortUrlCreateException e) {
        ResponseError response = new ResponseError(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ResponseError> handleException(RuntimeException e) {
        ResponseError response = new ResponseError(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
