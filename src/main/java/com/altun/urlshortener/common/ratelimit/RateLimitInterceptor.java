package com.altun.urlshortener.common.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final String CREATE_URL_PATH = "/api/v1/urls";
    private static final long CLEANUP_INTERVAL = 100;

    private final int requestLimit;
    private final long windowSeconds;
    private final Map<String, ClientWindow> clientWindows = new ConcurrentHashMap<>();
    private final AtomicLong requestCounter = new AtomicLong();

    public RateLimitInterceptor(
            @Value("${app.rate-limit.requests:10}") int requestLimit,
            @Value("${app.rate-limit.window-seconds:60}") long windowSeconds
    ) {
        if (requestLimit < 1 || windowSeconds < 1) {
            throw new IllegalArgumentException("Rate limit değerleri sıfırdan büyük olmalıdır");
        }
        this.requestLimit = requestLimit;
        this.windowSeconds = windowSeconds;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        if (!HttpMethod.POST.matches(request.getMethod())
                || !CREATE_URL_PATH.equals(request.getRequestURI())) {
            return true;
        }

        long now = Instant.now().getEpochSecond();
        cleanupExpiredWindows(now);

        AtomicReference<RateLimitDecision> decisionReference = new AtomicReference<>();
        String clientKey = resolveClientKey(request);

        clientWindows.compute(clientKey, (key, currentWindow) -> {
            ClientWindow activeWindow = currentWindow;
            if (activeWindow == null || now >= activeWindow.resetAt()) {
                activeWindow = new ClientWindow(now + windowSeconds, 0);
            }

            if (activeWindow.requestCount() >= requestLimit) {
                decisionReference.set(new RateLimitDecision(
                        false,
                        0,
                        Math.max(1, activeWindow.resetAt() - now)
                ));
                return activeWindow;
            }

            ClientWindow updatedWindow = new ClientWindow(
                    activeWindow.resetAt(),
                    activeWindow.requestCount() + 1
            );
            decisionReference.set(new RateLimitDecision(
                    true,
                    requestLimit - updatedWindow.requestCount(),
                    Math.max(1, updatedWindow.resetAt() - now)
            ));
            return updatedWindow;
        });

        RateLimitDecision decision = decisionReference.get();
        response.setHeader("X-RateLimit-Limit", String.valueOf(requestLimit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(decision.remaining()));

        if (!decision.allowed()) {
            response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(decision.retryAfterSeconds()));
            throw new RateLimitExceededException(decision.retryAfterSeconds());
        }

        return true;
    }

    private String resolveClientKey(HttpServletRequest request) {
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    private void cleanupExpiredWindows(long now) {
        if (requestCounter.incrementAndGet() % CLEANUP_INTERVAL == 0) {
            clientWindows.entrySet().removeIf(entry -> now >= entry.getValue().resetAt());
        }
    }

    private record ClientWindow(long resetAt, int requestCount) {
    }

    private record RateLimitDecision(boolean allowed, int remaining, long retryAfterSeconds) {
    }
}
