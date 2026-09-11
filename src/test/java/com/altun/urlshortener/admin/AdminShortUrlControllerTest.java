package com.altun.urlshortener.admin;

import com.altun.urlshortener.admin.dto.AdminShortUrlResponseDto;
import com.altun.urlshortener.common.config.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.ServletWebSecurityAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminShortUrlController.class, AdminPageController.class})
@Import(SecurityConfiguration.class)
@ImportAutoConfiguration({
        SecurityAutoConfiguration.class,
        ServletWebSecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
})
class AdminShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminShortUrlService adminShortUrlService;

    @Test
    void adminPageRedirectsAnonymousUserToLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "/login"));
    }

    @Test
    void adminCanListUrls() throws Exception {
        AdminShortUrlResponseDto item = response(true);
        when(adminShortUrlService.findAll(0, 20))
                .thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/api/v1/admin/urls")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("Ab12Cd34"))
                .andExpect(jsonPath("$.content[0].visitCount").value(9));
    }

    @Test
    void statusUpdateRequiresCsrfToken() throws Exception {
        mockMvc.perform(patch("/api/v1/admin/urls/1/status")
                        .with(user("admin").roles("ADMIN"))
                        .contentType("application/json")
                        .content("{\"active\":false}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanUpdateStatus() throws Exception {
        when(adminShortUrlService.updateStatus(1L, false)).thenReturn(response(false));

        mockMvc.perform(patch("/api/v1/admin/urls/1/status")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"active\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        verify(adminShortUrlService).updateStatus(1L, false);
    }

    @Test
    void adminCanDeleteUrl() throws Exception {
        mockMvc.perform(delete("/api/v1/admin/urls/1")
                        .with(user("admin").roles("ADMIN"))
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(adminShortUrlService).delete(1L);
    }

    private AdminShortUrlResponseDto response(boolean active) {
        return new AdminShortUrlResponseDto(
                1L,
                "Ab12Cd34",
                "https://example.com",
                LocalDateTime.now(),
                null,
                active,
                false,
                9L
        );
    }
}
