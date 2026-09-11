package com.altun.urlshortener.shorturl;

public class ShortUrlInactiveException extends RuntimeException {

    public ShortUrlInactiveException(String code) {
        super("Kısa URL pasif durumda: " + code);
    }
}
