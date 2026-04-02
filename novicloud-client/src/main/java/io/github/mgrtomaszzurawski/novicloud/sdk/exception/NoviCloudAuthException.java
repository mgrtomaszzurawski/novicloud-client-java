/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

/**
 * Thrown when the server rejects the request due to authentication or authorization failure.
 *
 * <p>HTTP status codes that produce this exception: 401 (Unauthorized), 403 (Forbidden).
 *
 * <p>Common causes: incorrect account name or password, account not active, or
 * insufficient permissions for the requested resource.
 * @since 1.0.0
 */
public class NoviCloudAuthException extends NoviCloudException {

    /** {@inheritDoc} */
    public NoviCloudAuthException(String message, Throwable cause, int statusCode, String responseBody) {
        super(message, cause, statusCode, responseBody);
    }
}
