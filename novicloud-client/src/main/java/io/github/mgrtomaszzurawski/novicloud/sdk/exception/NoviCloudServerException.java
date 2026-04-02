/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

/**
 * Thrown when the server responds with an HTTP 5xx error.
 *
 * <p>HTTP status codes that produce this exception: 500, 502, 503, 504, and any other 5xx.
 *
 * <p>The SDK automatically retries server errors when
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy#retryOn5xx()} is enabled
 * (the default). This exception is only surfaced when all retry attempts are exhausted
 * or retries are disabled.
 * @since 1.0.0
 */
public class NoviCloudServerException extends NoviCloudException {

    /** {@inheritDoc} */
    public NoviCloudServerException(String message, Throwable cause, int statusCode, String responseBody) {
        super(message, cause, statusCode, responseBody);
    }
}
