package com.altun.urlshortener.admin.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateShortUrlStatusRequestDto(
        @NotNull(message = "Aktiflik durumu zorunludur") Boolean active
) {
}
