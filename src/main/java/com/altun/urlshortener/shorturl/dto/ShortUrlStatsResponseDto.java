package com.altun.urlshortener.shorturl.dto;

import com.altun.urlshortener.shorturl.ShortUrl;

import java.time.LocalDateTime;

public record ShortUrlStatsResponseDto(
        String code,
        String originalUrl,
        long visitCount,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
) {

    public static ShortUrlStatsResponseDto from(ShortUrl shortUrl, long visitCount) {
        return new ShortUrlStatsResponseDto(
                shortUrl.getCode(),
                shortUrl.getOriginalUrl(),
                visitCount,
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt()
        );
    }
}
