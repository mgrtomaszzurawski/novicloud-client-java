/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

/**
 * Thrown when a network-level error prevents the request from completing.
 *
 * <p>Wraps {@link java.io.IOException} (connection refused, read timeout, broken pipe)
 * and {@link InterruptedException} (thread interrupted while waiting for response).
 * Unlike HTTP error exceptions, this exception carries no HTTP status code from the server
 * - use {@code getStatusCode() == 0} to detect it programmatically.
 *
 * <p>Not automatically retried by the SDK (only HTTP 429 and 5xx are retried).
 * @since 1.0.0
 */
public class NoviCloudNetworkException extends NoviCloudException {

    /** {@inheritDoc} */
    public NoviCloudNetworkException(String message, Throwable cause, int statusCode, String responseBody) {
        super(message, cause, statusCode, responseBody);
    }
}
