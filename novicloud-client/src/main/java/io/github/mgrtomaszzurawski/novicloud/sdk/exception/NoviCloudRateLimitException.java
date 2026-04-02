/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

/**
 * Thrown when the server responds with HTTP 429 (Too Many Requests).
 *
 * <p>The SDK automatically retries rate-limited requests when
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy#retryOn429()} is enabled
 * (the default). This exception is only surfaced when all retry attempts are exhausted
 * or retries are disabled.
 *
 * <p>Use {@link #getRetryAfterSeconds()} to determine how long to wait before the next
 * manual retry attempt.
 * @since 1.0.0
 */
public class NoviCloudRateLimitException extends NoviCloudException {

    private final long retryAfterSeconds;

    /**
     * Constructs a new rate-limit exception.
     *
     * @param message             human-readable description of the failed operation
     * @param cause               the underlying API exception
     * @param statusCode          HTTP status code (429)
     * @param responseBody        raw response body, or {@code null}
     * @param retryAfterSeconds   value parsed from the {@code Retry-After} response header;
     *                            {@code 0} if the header was absent or unparseable
     */
    public NoviCloudRateLimitException(String message, Throwable cause, int statusCode,
            String responseBody, long retryAfterSeconds)
    {
        super(message, cause, statusCode, responseBody);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * Returns the number of seconds the server requests the client to wait before retrying.
     * Parsed from the {@code Retry-After} response header. Returns {@code 0} if the header
     * was absent or could not be parsed.
     *
     * @return suggested wait time in seconds, or {@code 0}
     */
    public long getRetryAfterSeconds() { return retryAfterSeconds; }
}
