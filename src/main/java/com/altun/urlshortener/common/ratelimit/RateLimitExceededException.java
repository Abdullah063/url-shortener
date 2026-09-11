package com.altun.urlshortener.common.ratelimit;

public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;

    public RateLimitExceededException(long retryAfterSeconds) {
        super("Çok fazla bağlantı oluşturdunuz. Lütfen "
                + retryAfterSeconds
                + " saniye sonra tekrar deneyin.");
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
