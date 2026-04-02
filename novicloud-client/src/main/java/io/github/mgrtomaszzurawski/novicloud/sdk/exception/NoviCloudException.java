/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

import io.github.mgrtomaszzurawski.novicloud.client.ApiException;

import java.net.http.HttpHeaders;

/**
 * Base class for all exceptions thrown by the NoviCloud SDK.
 *
 * <p>Every API call that fails with an HTTP error code or a network problem
 * throws a subclass of this exception. Callers can catch {@code NoviCloudException}
 * to handle all SDK errors uniformly, or catch specific subclasses to handle
 * particular failure modes differently:
 *
 * <pre>{@code
 * try {
 *     Towar t = client.towary().getById(42L).getDane();
 * } catch (NoviCloudNotFoundException e) {
 *     // record does not exist (404/410)
 * } catch (NoviCloudAuthException e) {
 *     // bad credentials (401/403)
 * } catch (NoviCloudRateLimitException e) {
 *     // rate limited (429) - retry after e.getRetryAfterSeconds()
 * } catch (NoviCloudException e) {
 *     // any other SDK error
 * }
 * }</pre>
 *
 * <p>All subclasses are unchecked. The SDK maps HTTP codes to typed subclasses
 * automatically via {@link #of(String, ApiException)}.
 *
 * @see NoviCloudAuthException
 * @see NoviCloudNotFoundException
 * @see NoviCloudRateLimitException
 * @see NoviCloudServerException
 * @see NoviCloudNetworkException
 * @since 1.0.0
 */
public class NoviCloudException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String RETRY_AFTER_HEADER = "Retry-After";
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_FORBIDDEN = 403;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_GONE = 410;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    private static final long DEFAULT_RETRY_AFTER = 0L;

    private final int statusCode;
    private final String responseBody;

    /**
     * Constructs a new exception with the given detail message, cause, HTTP status code,
     * and raw response body.
     *
     * @param message      human-readable description of the failed operation
     * @param cause        the underlying {@link io.github.mgrtomaszzurawski.novicloud.client.ApiException}
     * @param statusCode   HTTP status code returned by the server (0 if not available)
     * @param responseBody raw response body as a string, or {@code null} if not available
     */
    public NoviCloudException(String message, Throwable cause, int statusCode, String responseBody) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Returns the HTTP status code associated with this exception.
     *
     * @return HTTP status code, or {@code 0} if the error occurred before a response was received
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the raw response body returned by the server, if available.
     *
     * <p><strong>Privacy note:</strong> on HTTP 400 (Bad Request) the server echoes back
     * rejected parameter names and values in {@code dane.par_niewlasciwe}. If the original
     * request contained personal data (e.g. contractor names, loyalty card holder details),
     * that data will be present in this string. Avoid logging or persisting the response
     * body without sanitization when processing user-supplied personal data.
     * To suppress SDK logging entirely, set the
     * {@code io.github.mgrtomaszzurawski.novicloud.sdk} logger to {@code OFF}
     * in your SLF4J configuration.
     *
     * @return response body as a string, or {@code null}
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Maps an {@link io.github.mgrtomaszzurawski.novicloud.client.ApiException} to the
     * appropriate typed subclass based on the HTTP status code:
     * <ul>
     *   <li>401, 403 - {@link NoviCloudAuthException}</li>
     *   <li>404, 410 - {@link NoviCloudNotFoundException}</li>
     *   <li>429 - {@link NoviCloudRateLimitException} (parses {@code Retry-After} header)</li>
     *   <li>5xx - {@link NoviCloudServerException}</li>
     *   <li>other - {@code NoviCloudException}</li>
     * </ul>
     *
     * @param message human-readable description of the failed operation
     * @param e       the underlying API exception
     * @return typed subclass of {@code NoviCloudException}
     */
    public static NoviCloudException of(String message, ApiException e) {
        int code = e.getCode();
        String body = e.getResponseBody();
        if (code == HTTP_UNAUTHORIZED || code == HTTP_FORBIDDEN) {
            return new NoviCloudAuthException(message, e, code, body);
        }
        if (code == HTTP_NOT_FOUND || code == HTTP_GONE) {
            return new NoviCloudNotFoundException(message, e, code, body);
        }
        if (code == HTTP_TOO_MANY_REQUESTS) {
            long retryAfter = parseRetryAfter(e.getResponseHeaders());
            return new NoviCloudRateLimitException(message, e, code, body, retryAfter);
        }
        if (code >= HTTP_INTERNAL_SERVER_ERROR) {
            return new NoviCloudServerException(message, e, code, body);
        }
        return new NoviCloudException(message, e, code, body);
    }

    private static long parseRetryAfter(HttpHeaders headers) {
        if (headers == null) {
            return DEFAULT_RETRY_AFTER;
        }
        return headers.firstValue(RETRY_AFTER_HEADER)
                .map(val -> {
                    try {
                        return Long.parseLong(val.trim());
                    } catch (NumberFormatException ex) {
                        return DEFAULT_RETRY_AFTER;
                    }
                })
                .orElse(DEFAULT_RETRY_AFTER);
    }
}
