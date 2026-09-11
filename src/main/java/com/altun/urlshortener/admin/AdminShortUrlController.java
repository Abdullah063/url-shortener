package com.altun.urlshortener.admin;

import com.altun.urlshortener.admin.dto.AdminShortUrlPageResponseDto;
import com.altun.urlshortener.admin.dto.AdminShortUrlResponseDto;
import com.altun.urlshortener.admin.dto.UpdateShortUrlStatusRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin", description = "Yönetici bağlantı işlemleri")
public class AdminShortUrlController {

    private final AdminShortUrlService adminShortUrlService;

    public AdminShortUrlController(AdminShortUrlService adminShortUrlService) {
        this.adminShortUrlService = adminShortUrlService;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of(
                "headerName", csrfToken.getHeaderName(),
                "token", csrfToken.getToken()
        );
    }

    @GetMapping("/urls")
    @Operation(summary = "Bağlantıları sayfalı olarak listeler")
    public AdminShortUrlPageResponseDto findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 100);
        return AdminShortUrlPageResponseDto.from(
                adminShortUrlService.findAll(safePage, safeSize)
        );
    }

    @PatchMapping("/urls/{id}/status")
    @Operation(summary = "Bağlantıyı aktif veya pasif yapar")
    public AdminShortUrlResponseDto updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateShortUrlStatusRequestDto request
    ) {
        return adminShortUrlService.updateStatus(id, request.active());
    }

    @DeleteMapping("/urls/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Bağlantıyı ve ziyaret kayıtlarını siler")
    public void delete(@PathVariable Long id) {
        adminShortUrlService.delete(id);
    }
}
