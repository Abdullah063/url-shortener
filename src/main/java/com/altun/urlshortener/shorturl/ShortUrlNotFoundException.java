package com.altun.urlshortener.shorturl;

public class ShortUrlNotFoundException extends RuntimeException {

    public ShortUrlNotFoundException(String code) {
        super("Kısa URL bulunamadı: " + code);
    }
}
