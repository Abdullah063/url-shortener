package com.altun.urlshortener.shorturl.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateShortUrlRequestDto(
        @NotBlank(message = "URL boş olamaz")
        @Pattern(
                regexp = "^https?://.+$",
                message = "URL http:// veya https:// ile başlamalıdır"
        )
        String originalUrl,
        @Future(message = "Son kullanma tarihi gelecekte olmalıdır")
        LocalDateTime expiresAt
) {
}
