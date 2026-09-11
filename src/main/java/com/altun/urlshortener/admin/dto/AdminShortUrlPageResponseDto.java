package com.altun.urlshortener.admin.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record AdminShortUrlPageResponseDto(
        List<AdminShortUrlResponseDto> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static AdminShortUrlPageResponseDto from(Page<AdminShortUrlResponseDto> result) {
        return new AdminShortUrlPageResponseDto(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }
}
