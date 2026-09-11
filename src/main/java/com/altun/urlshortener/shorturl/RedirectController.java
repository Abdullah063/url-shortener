package com.altun.urlshortener.shorturl;

import com.altun.urlshortener.visit.UrlVisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Tag(name = "Redirect", description = "Kısa bağlantı yönlendirmesi")
public class RedirectController {

    private final ShortUrlService shortUrlService;
    private final UrlVisitService urlVisitService;

    public RedirectController(
            ShortUrlService shortUrlService,
            UrlVisitService urlVisitService
    ) {
        this.shortUrlService = shortUrlService;
        this.urlVisitService = urlVisitService;
    }

    @GetMapping("/{code:[A-Za-z0-9]{8}}")
    @Operation(summary = "Kısa kodu asıl bağlantıya yönlendirir")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        ShortUrl shortUrl = shortUrlService.findByCode(code);
        urlVisitService.recordVisit(shortUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(shortUrl.getOriginalUrl()))
                .build();
    }
}
