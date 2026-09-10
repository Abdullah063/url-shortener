package com.altun.urlshortener.shorturl;

import com.altun.urlshortener.common.error.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ShortUrlController.class, RedirectController.class})
@Import(GlobalExceptionHandler.class)
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShortUrlService shortUrlService;

    @Test
    void createReturnsCreatedResponse() throws Exception {
        ShortUrl shortUrl = new ShortUrl("Ab12Cd34", "https://example.com");
        when(shortUrlService.createShortUrl("https://example.com", null)).thenReturn(shortUrl);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"originalUrl":"https://example.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("Ab12Cd34"))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com"));
    }

    @Test
    void createReturnsBadRequestForInvalidUrl() throws Exception {
        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"originalUrl":"example.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("İstek doğrulanamadı"))
                .andExpect(jsonPath("$.fieldErrors.originalUrl")
                        .value("URL http:// veya https:// ile başlamalıdır"));
    }

    @Test
    void createReturnsBadRequestForPastExpiration() throws Exception {
        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "originalUrl":"https://example.com",
                                  "expiresAt":"2020-01-01T00:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.expiresAt")
                        .value("Son kullanma tarihi gelecekte olmalıdır"));
    }

    @Test
    void findByCodeReturnsNotFoundForUnknownCode() throws Exception {
        when(shortUrlService.findByCode("missing1"))
                .thenThrow(new ShortUrlNotFoundException("missing1"));

        mockMvc.perform(get("/api/v1/urls/missing1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Kısa URL bulunamadı: missing1"))
                .andExpect(jsonPath("$.path").value("/api/v1/urls/missing1"));
    }

    @Test
    void redirectReturnsFoundWithLocationHeader() throws Exception {
        ShortUrl shortUrl = new ShortUrl("Ab12Cd34", "https://example.com");
        when(shortUrlService.findByCode("Ab12Cd34")).thenReturn(shortUrl);

        mockMvc.perform(get("/Ab12Cd34"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));
    }

    @Test
    void redirectReturnsGoneForExpiredUrl() throws Exception {
        when(shortUrlService.findByCode("expired1"))
                .thenThrow(new ShortUrlExpiredException("expired1"));

        mockMvc.perform(get("/expired1"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.message")
                        .value("Kısa URL'nin süresi doldu: expired1"));
    }
}
