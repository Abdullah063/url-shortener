package com.altun.urlshortener.admin;

public class AdminShortUrlNotFoundException extends RuntimeException {

    public AdminShortUrlNotFoundException(Long id) {
        super("Bağlantı bulunamadı: " + id);
    }
}
