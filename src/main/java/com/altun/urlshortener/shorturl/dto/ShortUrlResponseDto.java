package com.altun.urlshortener.shorturl.dto;

import com.altun.urlshortener.shorturl.ShortUrl;

import java.time.LocalDateTime;

public record ShortUrlResponseDto(
        Long id,
        String code,
        String originalUrl,
        LocalDateTime createdAt
) {

    public static ShortUrlResponseDto from(ShortUrl shortUrl) {
        return new ShortUrlResponseDto(
                shortUrl.getId(),
                shortUrl.getCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getCreatedAt()
        );
    }
}
