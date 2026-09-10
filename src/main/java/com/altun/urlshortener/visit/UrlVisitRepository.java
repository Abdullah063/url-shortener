package com.altun.urlshortener.visit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlVisitRepository extends JpaRepository<UrlVisit, Long> {

    long countByShortUrlId(Long shortUrlId);
}
