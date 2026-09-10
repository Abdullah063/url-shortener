package com.altun.urlshortener.visit;

import com.altun.urlshortener.shorturl.ShortUrl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlVisitServiceTest {

    @Mock
    private UrlVisitRepository urlVisitRepository;

    private UrlVisitService urlVisitService;

    @BeforeEach
    void setUp() {
        urlVisitService = new UrlVisitService(urlVisitRepository);
    }

    @Test
    void recordVisitSavesVisitForShortUrl() {
        ShortUrl shortUrl = new ShortUrl("Ab12Cd34", "https://example.com");
        ArgumentCaptor<UrlVisit> visitCaptor = ArgumentCaptor.forClass(UrlVisit.class);

        urlVisitService.recordVisit(shortUrl);

        verify(urlVisitRepository).save(visitCaptor.capture());
        assertEquals(shortUrl, visitCaptor.getValue().getShortUrl());
        assertNotNull(visitCaptor.getValue().getVisitedAt());
    }

    @Test
    void countVisitsUsesShortUrlId() {
        ShortUrl shortUrl = new ShortUrl("Ab12Cd34", "https://example.com");
        ReflectionTestUtils.setField(shortUrl, "id", 42L);
        when(urlVisitRepository.countByShortUrlId(42L)).thenReturn(7L);

        long result = urlVisitService.countVisits(shortUrl);

        assertEquals(7L, result);
        verify(urlVisitRepository).countByShortUrlId(42L);
    }
}
