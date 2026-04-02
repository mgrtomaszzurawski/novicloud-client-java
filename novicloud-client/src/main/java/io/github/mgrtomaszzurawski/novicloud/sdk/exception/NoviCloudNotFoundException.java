/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

/**
 * Thrown when the requested resource does not exist on the server.
 *
 * <p>HTTP status codes that produce this exception: 404 (Not Found), 410 (Gone).
 *
 * <p>Typically thrown by {@code getById} methods when no record with the given ID exists,
 * or when a previously existing record has been permanently deleted (410).
 * @since 1.0.0
 */
public class NoviCloudNotFoundException extends NoviCloudException {

    /** {@inheritDoc} */
    public NoviCloudNotFoundException(String message, Throwable cause, int statusCode, String responseBody) {
        super(message, cause, statusCode, responseBody);
    }
}
