package com.altun.urlshortener.admin.dto;

import com.altun.urlshortener.shorturl.ShortUrl;

import java.time.LocalDateTime;

public record AdminShortUrlResponseDto(
        Long id,
        String code,
        String originalUrl,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        boolean active,
        boolean expired,
        long visitCount
) {

    public static AdminShortUrlResponseDto from(ShortUrl shortUrl, long visitCount) {
        return new AdminShortUrlResponseDto(
                shortUrl.getId(),
                shortUrl.getCode(),
                shortUrl.getOriginalUrl(),
                shortUrl.getCreatedAt(),
                shortUrl.getExpiresAt(),
                shortUrl.isActive(),
                shortUrl.isExpired(),
                visitCount
        );
    }
}
