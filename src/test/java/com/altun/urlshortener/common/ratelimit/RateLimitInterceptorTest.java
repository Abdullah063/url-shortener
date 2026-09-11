package com.altun.urlshortener.common.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimitInterceptorTest {

    @Test
    void rejectsCreateRequestWhenLimitIsExceeded() {
        RateLimitInterceptor interceptor = new RateLimitInterceptor(2, 60);
        MockHttpServletRequest request = createRequest("203.0.113.10");

        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        MockHttpServletResponse secondResponse = new MockHttpServletResponse();
        MockHttpServletResponse rejectedResponse = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, firstResponse, new Object()));
        assertTrue(interceptor.preHandle(request, secondResponse, new Object()));

        assertThrows(
                RateLimitExceededException.class,
                () -> interceptor.preHandle(request, rejectedResponse, new Object())
        );
        assertEquals("2", rejectedResponse.getHeader("X-RateLimit-Limit"));
        assertEquals("0", rejectedResponse.getHeader("X-RateLimit-Remaining"));
        assertTrue(Long.parseLong(rejectedResponse.getHeader("Retry-After")) > 0);
    }

    @Test
    void allowsRequestsFromDifferentClients() {
        RateLimitInterceptor interceptor = new RateLimitInterceptor(1, 60);

        assertTrue(interceptor.preHandle(
                createRequest("203.0.113.10"),
                new MockHttpServletResponse(),
                new Object()
        ));
        assertTrue(interceptor.preHandle(
                createRequest("203.0.113.11"),
                new MockHttpServletResponse(),
                new Object()
        ));
    }

    private MockHttpServletRequest createRequest(String clientIp) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/urls");
        request.addHeader("X-Real-IP", clientIp);
        return request;
    }
}
