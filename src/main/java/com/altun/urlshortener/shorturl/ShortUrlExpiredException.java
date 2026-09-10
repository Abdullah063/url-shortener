package com.altun.urlshortener.shorturl;

public class ShortUrlExpiredException extends RuntimeException {

    public ShortUrlExpiredException(String code) {
        super("Kısa URL'nin süresi doldu: " + code);
    }
}
