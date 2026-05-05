/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

/**
 * Thrown when the NoviCloud account does not have access to the REST API.
 *
 * <p>HTTP status code: 402 (Payment Required). Per the official NoviCloud
 * REST API documentation: <em>"odmowa dostepu z powodu nie wykupionej opcji
 * REST API NoviCloud"</em> - the REST API option is not subscribed,
 * disabled, suspended, or not ordered for this account.
 *
 * <p>Distinct from {@link NoviCloudAuthException} (401/403, credential
 * problem) - here the credentials are valid but the account is not
 * entitled to use the API.
 *
 * @since 2.0.0
 */
public class NoviCloudAccessException extends NoviCloudException {

    private static final long serialVersionUID = 1L;

    /** {@inheritDoc} */
    public NoviCloudAccessException(String message, Throwable cause, int statusCode, String responseBody) {
        super(message, cause, statusCode, responseBody);
    }
}
