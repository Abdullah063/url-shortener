package com.altun.urlshortener.shorturl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public Optional<ShortUrl> findByCode(String code) {
        return shortUrlRepository.findByCode(code);
    }

    public ShortUrl createShortUrl(String originalUrl) {

        String code = UUID.randomUUID().toString().substring(0, 8);
        ShortUrl shortUrl = new ShortUrl(code, originalUrl);
        return shortUrlRepository.save(shortUrl);
    }
}
