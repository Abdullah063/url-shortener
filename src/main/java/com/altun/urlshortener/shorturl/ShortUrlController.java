package com.altun.urlshortener.shorturl;

import org.springframework.http.HttpStatus;
import com.altun.urlshortener.shorturl.dto.CreateShortUrlRequestDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/urls")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ShortUrl create(@Valid @RequestBody CreateShortUrlRequestDto request) {
        return shortUrlService.createShortUrl(request.originalUrl());
    }

    @GetMapping("/{code}")
    public Optional<ShortUrl> findByCode(@PathVariable String code) {
        return shortUrlService.findByCode(code);
    }
}
