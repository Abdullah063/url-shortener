package com.altun.urlshortener.shorturl;

import com.altun.urlshortener.shorturl.dto.CreateShortUrlRequestDto;
import com.altun.urlshortener.shorturl.dto.ShortUrlResponseDto;
import com.altun.urlshortener.shorturl.dto.ShortUrlStatsResponseDto;
import com.altun.urlshortener.visit.UrlVisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Short URLs", description = "Kısa bağlantı oluşturma ve sorgulama işlemleri")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final UrlVisitService urlVisitService;

    public ShortUrlController(
            ShortUrlService shortUrlService,
            UrlVisitService urlVisitService
    ) {
        this.shortUrlService = shortUrlService;
        this.urlVisitService = urlVisitService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(summary = "Yeni bir kısa bağlantı oluşturur")
    public ShortUrlResponseDto create(@Valid @RequestBody CreateShortUrlRequestDto request) {
        ShortUrl shortUrl = shortUrlService.createShortUrl(
                request.originalUrl(),
                request.expiresAt()
        );
        return ShortUrlResponseDto.from(shortUrl);
    }

    @GetMapping("/{code}")
    @Operation(summary = "Kısa bağlantı bilgilerini getirir")
    public ShortUrlResponseDto findByCode(@PathVariable String code) {
        return ShortUrlResponseDto.from(shortUrlService.findByCode(code));
    }

    @GetMapping("/{code}/stats")
    @Operation(summary = "Kısa bağlantının ziyaret istatistiğini getirir")
    public ShortUrlStatsResponseDto getStats(@PathVariable String code) {
        ShortUrl shortUrl = shortUrlService.findByCode(code);
        long visitCount = urlVisitService.countVisits(shortUrl);
        return ShortUrlStatsResponseDto.from(shortUrl, visitCount);
    }
}
