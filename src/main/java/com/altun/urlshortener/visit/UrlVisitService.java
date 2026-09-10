package com.altun.urlshortener.visit;

import com.altun.urlshortener.shorturl.ShortUrl;
import org.springframework.stereotype.Service;

@Service
public class UrlVisitService {

    private final UrlVisitRepository urlVisitRepository;

    public UrlVisitService(UrlVisitRepository urlVisitRepository) {
        this.urlVisitRepository = urlVisitRepository;
    }

    public void recordVisit(ShortUrl shortUrl) {
        urlVisitRepository.save(new UrlVisit(shortUrl));
    }

    public long countVisits(ShortUrl shortUrl) {
        return urlVisitRepository.countByShortUrlId(shortUrl.getId());
    }
}
