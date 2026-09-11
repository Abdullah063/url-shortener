package com.altun.urlshortener.admin;

import com.altun.urlshortener.admin.dto.AdminShortUrlResponseDto;
import com.altun.urlshortener.shorturl.ShortUrl;
import com.altun.urlshortener.shorturl.ShortUrlRepository;
import com.altun.urlshortener.visit.UrlVisitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final UrlVisitService urlVisitService;

    public AdminShortUrlService(
            ShortUrlRepository shortUrlRepository,
            UrlVisitService urlVisitService
    ) {
        this.shortUrlRepository = shortUrlRepository;
        this.urlVisitService = urlVisitService;
    }

    @Transactional(readOnly = true)
    public Page<AdminShortUrlResponseDto> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return shortUrlRepository.findAll(pageRequest)
                .map(shortUrl -> AdminShortUrlResponseDto.from(
                        shortUrl,
                        urlVisitService.countVisits(shortUrl)
                ));
    }

    @Transactional
    public AdminShortUrlResponseDto updateStatus(Long id, boolean active) {
        ShortUrl shortUrl = findById(id);
        shortUrl.setActive(active);
        return AdminShortUrlResponseDto.from(
                shortUrlRepository.save(shortUrl),
                urlVisitService.countVisits(shortUrl)
        );
    }

    @Transactional
    public void delete(Long id) {
        ShortUrl shortUrl = findById(id);
        shortUrlRepository.delete(shortUrl);
    }

    private ShortUrl findById(Long id) {
        return shortUrlRepository.findById(id)
                .orElseThrow(() -> new AdminShortUrlNotFoundException(id));
    }
}
