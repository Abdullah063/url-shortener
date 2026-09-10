package com.altun.urlshortener.shorturl;

import com.altun.urlshortener.shorturl.dto.CreateShortUrlRequestDto;
import com.altun.urlshortener.shorturl.dto.ShortUrlResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/urls")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ShortUrlResponseDto create(@Valid @RequestBody CreateShortUrlRequestDto request) {
        ShortUrl shortUrl = shortUrlService.createShortUrl(request.originalUrl());
        return ShortUrlResponseDto.from(shortUrl);
    }

    @GetMapping("/{code}")
    public ShortUrlResponseDto findByCode(@PathVariable String code) {
        return ShortUrlResponseDto.from(shortUrlService.findByCode(code));
    }
}
