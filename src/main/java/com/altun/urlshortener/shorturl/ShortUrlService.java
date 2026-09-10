package com.altun.urlshortener.shorturl;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ShortUrlService {

    private static final String CODE_CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    private final ShortUrlRepository shortUrlRepository;
    private final SecureRandom random = new SecureRandom();

    public ShortUrlService(ShortUrlRepository shortUrlRepository) {
        this.shortUrlRepository = shortUrlRepository;
    }

    public Optional<ShortUrl> findByCode(String code) {
        return shortUrlRepository.findByCode(code);
    }

    public ShortUrl createShortUrl(String originalUrl) {
        String code = generateUniqueCode();
        ShortUrl shortUrl = new ShortUrl(code, originalUrl);
        return shortUrlRepository.save(shortUrl);
    }

    private String generateUniqueCode() {
        String code;

        do {
            code = generateCode();
        } while (shortUrlRepository.existsByCode(code));

        return code;
    }

    private String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CODE_CHARACTERS.length());
            code.append(CODE_CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}
