/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
 *     Towar t = client.towary().getById(42L);
 *     System.out.println(t.nazwa());
 * } catch (NoviCloudNotFoundException e) {
 *     // record does not exist (404/410, or HTTP 200 with empty dane)
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
 * automatically via {@link #of(String, Throwable, int, HttpHeaders, String)}.
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
    private static final int HTTP_NETWORK_UNKNOWN = 0;
    private static final int HTTP_UNAUTHORIZED = 401;
    private static final int HTTP_PAYMENT_REQUIRED = 402;
    private static final int HTTP_FORBIDDEN = 403;
    private static final int HTTP_NOT_FOUND = 404;
    private static final int HTTP_GONE = 410;
    private static final int HTTP_TOO_MANY_REQUESTS = 429;
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    private static final int HTTP_OK = 200;
    private static final long DEFAULT_RETRY_AFTER = 0L;
    private static final String EMPTY_DANE_FORMAT = "%s with id %s not found (HTTP 200 with empty dane)";
    private static final String FIELD_DANE = "dane";
    private static final String FIELD_PAR_NIEWLASCIWE = "par_niewlasciwe";
    private static final String FIELD_PAR_BLEDNA_WART = "par_bledna_wart";
    private static final ObjectMapper ERROR_DETAILS_MAPPER = new ObjectMapper();

    private final int statusCode;
    private final String responseBody;

    /**
     * Constructs a new exception with the given detail message, cause, HTTP status code,
     * and raw response body.
     *
     * @param message      human-readable description of the failed operation
     * @param cause        the underlying cause (a transport exception or a generated
     *                     low-level exception); may be {@code null}
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
     * Parses the response body as a NoviCloud error envelope and returns its
     * {@code dane} payload as structured {@link NoviCloudErrorDetails}.
     *
     * <p>Useful for HTTP 400 validation failures: the server reports the offending
     * parameters in {@code dane.par_niewlasciwe} (unknown names) and
     * {@code dane.par_bledna_wart} (rejected values). Callers can route those into
     * a UI or remediation logic without writing their own JSON parser.
     *
     * <p>Returns {@link Optional#empty()} when the body is missing, not JSON, or
     * when neither field is present in {@code dane}. Parsing is lazy; the parsed
     * value is not cached.
     *
     * @return structured error details, or empty if not parseable
     * @since 2.0.0
     */
    public Optional<NoviCloudErrorDetails> getErrorDetails() {
        if (responseBody == null || responseBody.isBlank()) {
            return Optional.empty();
        }
        try {
            JsonNode root = ERROR_DETAILS_MAPPER.readTree(responseBody);
            JsonNode dane = root.path(FIELD_DANE);
            if (dane.isMissingNode() || !dane.isObject()) {
                return Optional.empty();
            }
            List<String> niewlasciwe = stringArray(dane.path(FIELD_PAR_NIEWLASCIWE));
            List<String> blednaWart = stringArray(dane.path(FIELD_PAR_BLEDNA_WART));
            if (niewlasciwe.isEmpty() && blednaWart.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(new NoviCloudErrorDetails(niewlasciwe, blednaWart));
        } catch (IOException ex) {
            return Optional.empty();
        }
    }

    private static List<String> stringArray(JsonNode node) {
        if (node == null || !node.isArray()) {
            return List.of();
        }
        List<String> out = new ArrayList<>(node.size());
        node.forEach(item -> out.add(item.asText()));
        return out;
    }

    /**
     * Maps an underlying low-level failure to the appropriate typed subclass based on
     * the HTTP status code:
     * <ul>
     *   <li>401, 403 - {@link NoviCloudAuthException}</li>
     *   <li>402 - {@link NoviCloudAccessException} (account not entitled to REST API)</li>
     *   <li>404, 410 - {@link NoviCloudNotFoundException}</li>
     *   <li>429 - {@link NoviCloudRateLimitException} (parses {@code Retry-After} header)</li>
     *   <li>5xx - {@link NoviCloudServerException}</li>
     *   <li>code 0 with {@link java.io.IOException}/{@link InterruptedException} cause
     *       - {@link NoviCloudNetworkException}</li>
     *   <li>other - {@code NoviCloudException}</li>
     * </ul>
     *
     * <p>Since 2.0.0 this signature uses neutral types only - the SDK no longer
     * surfaces the generated low-level exception type in its public API.
     *
     * @param message      human-readable description of the failed operation
     * @param cause        the underlying cause (typically the generated low-level exception);
     *                     {@code null} when the failure is synthetic (for example, an empty
     *                     {@code dane} payload)
     * @param statusCode   HTTP status code, or {@code 0} for transport failures
     * @param headers      response headers if available; used to parse {@code Retry-After}
     * @param responseBody raw response body, or {@code null}
     * @return typed subclass of {@code NoviCloudException}
     * @since 2.0.0
     */
    public static NoviCloudException of(String message, Throwable cause, int statusCode,
                                        HttpHeaders headers, String responseBody)
    {
        if (statusCode == HTTP_NETWORK_UNKNOWN && isTransportFailure(cause)) {
            return new NoviCloudNetworkException(message, cause, statusCode, responseBody);
        }
        if (statusCode == HTTP_UNAUTHORIZED || statusCode == HTTP_FORBIDDEN) {
            return new NoviCloudAuthException(message, cause, statusCode, responseBody);
        }
        if (statusCode == HTTP_PAYMENT_REQUIRED) {
            return new NoviCloudAccessException(message, cause, statusCode, responseBody);
        }
        if (statusCode == HTTP_NOT_FOUND || statusCode == HTTP_GONE) {
            return new NoviCloudNotFoundException(message, cause, statusCode, responseBody);
        }
        if (statusCode == HTTP_TOO_MANY_REQUESTS) {
            long retryAfter = parseRetryAfter(headers);
            return new NoviCloudRateLimitException(message, cause, statusCode, responseBody, retryAfter);
        }
        if (statusCode >= HTTP_INTERNAL_SERVER_ERROR) {
            return new NoviCloudServerException(message, cause, statusCode, responseBody);
        }
        return new NoviCloudException(message, cause, statusCode, responseBody);
    }

    private static boolean isTransportFailure(Throwable cause) {
        return cause instanceof IOException || cause instanceof InterruptedException;
    }

    /**
     * Validates that a single-record response payload is non-null.
     *
     * <p>The NoviCloud API may return HTTP 200 with {@code "dane": null} for hard-deleted
     * or never-existed records (see ADR-033). Without this check the SDK would NPE when
     * mapping into the public record. This helper converts that case into a typed
     * {@link NoviCloudNotFoundException} so callers see a uniform contract.
     *
     * @param dane     the deserialized {@code dane} payload from a single-record response
     * @param resource human-readable resource name (e.g. {@code "towar"}, {@code "dokument"})
     * @param id       the requested identifier (used only in the exception message)
     * @param <T>      the payload type
     * @return {@code dane}, never {@code null}
     * @throws NoviCloudNotFoundException if {@code dane} is {@code null}
     * @since 2.0.0
     */
    public static <T> T requireDane(T dane, String resource, Object id) {
        if (dane == null) {
            String message = String.format(EMPTY_DANE_FORMAT, resource, id);
            throw new NoviCloudNotFoundException(message, null, HTTP_OK, null);
        }
        return dane;
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
