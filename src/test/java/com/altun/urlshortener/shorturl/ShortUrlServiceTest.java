package com.altun.urlshortener.shorturl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @Mock
    private ShortUrlRepository shortUrlRepository;

    private ShortUrlService shortUrlService;

    @BeforeEach
    void setUp() {
        shortUrlService = new ShortUrlService(shortUrlRepository);
    }

    @Test
    void createShortUrlGeneratesCodeAndSavesEntity() {
        when(shortUrlRepository.existsByCode(anyString())).thenReturn(false);
        when(shortUrlRepository.save(any(ShortUrl.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShortUrl result = shortUrlService.createShortUrl("https://example.com", null);

        assertEquals("https://example.com", result.getOriginalUrl());
        assertTrue(result.getCode().matches("[A-Za-z0-9]{8}"));
        verify(shortUrlRepository).existsByCode(result.getCode());
        verify(shortUrlRepository).save(result);
    }

    @Test
    void findByCodeReturnsEntityWhenItExists() {
        ShortUrl shortUrl = new ShortUrl("Ab12Cd34", "https://example.com");
        when(shortUrlRepository.findByCode("Ab12Cd34")).thenReturn(Optional.of(shortUrl));

        ShortUrl result = shortUrlService.findByCode("Ab12Cd34");

        assertEquals(shortUrl, result);
    }

    @Test
    void findByCodeThrowsExceptionWhenItDoesNotExist() {
        when(shortUrlRepository.findByCode("missing1")).thenReturn(Optional.empty());

        ShortUrlNotFoundException exception = assertThrows(
                ShortUrlNotFoundException.class,
                () -> shortUrlService.findByCode("missing1")
        );

        assertEquals("Kısa URL bulunamadı: missing1", exception.getMessage());
    }

    @Test
    void findByCodeThrowsExceptionWhenUrlIsExpired() {
        ShortUrl shortUrl = new ShortUrl(
                "expired1",
                "https://example.com",
                LocalDateTime.now().minusMinutes(1)
        );
        when(shortUrlRepository.findByCode("expired1")).thenReturn(Optional.of(shortUrl));

        ShortUrlExpiredException exception = assertThrows(
                ShortUrlExpiredException.class,
                () -> shortUrlService.findByCode("expired1")
        );

        assertEquals("Kısa URL'nin süresi doldu: expired1", exception.getMessage());
    }

    @Test
    void findByCodeThrowsExceptionWhenUrlIsInactive() {
        ShortUrl shortUrl = new ShortUrl("inactive", "https://example.com");
        shortUrl.setActive(false);
        when(shortUrlRepository.findByCode("inactive")).thenReturn(Optional.of(shortUrl));

        ShortUrlInactiveException exception = assertThrows(
                ShortUrlInactiveException.class,
                () -> shortUrlService.findByCode("inactive")
        );

        assertEquals("Kısa URL pasif durumda: inactive", exception.getMessage());
    }
}
