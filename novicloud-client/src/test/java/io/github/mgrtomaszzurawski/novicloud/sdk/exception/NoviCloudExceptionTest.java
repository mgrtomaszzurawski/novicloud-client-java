/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.exception;

import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import org.junit.jupiter.api.Test;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.Map;

import static io.github.mgrtomaszzurawski.novicloud.sdk.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class NoviCloudExceptionTest {

    private static final String MSG = "msg";
    private static final String TEST_ERROR = "test error";
    private static final String RETRY_AFTER_HEADER_LC = "retry-after";
    private static final String RETRY_AFTER_VALUE = "42";
    private static final String INVALID_RETRY_AFTER = "not-a-number";
    private static final long EXPECTED_RETRY_AFTER_42 = 42L;
    private static final long EXPECTED_RETRY_AFTER_ZERO = 0L;
    private static final String EXCEPTION_BODY = "body";

    private static ApiException apiEx(int code) {
        return new ApiException(code, TEST_ERROR);
    }

    private static ApiException apiEx(int code, HttpHeaders headers) {
        return new ApiException(TEST_ERROR, null, code, headers, EXCEPTION_BODY);
    }

    @Test
    void of_whenCode401_returnsAuthException() {
        // given
        ApiException cause = apiEx(HTTP_UNAUTHORIZED);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudAuthException.class, ex);
        assertEquals(HTTP_UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void of_whenCode403_returnsAuthException() {
        // given
        ApiException cause = apiEx(HTTP_FORBIDDEN);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudAuthException.class, ex);
        assertEquals(HTTP_FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void of_whenCode404_returnsNotFoundException() {
        // given
        ApiException cause = apiEx(HTTP_NOT_FOUND);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudNotFoundException.class, ex);
        assertEquals(HTTP_NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void of_whenCode410_returnsNotFoundException() {
        // given
        ApiException cause = apiEx(HTTP_GONE);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudNotFoundException.class, ex);
        assertEquals(HTTP_GONE, ex.getStatusCode());
    }

    @Test
    void of_whenCode429_returnsRateLimitException() {
        // given
        ApiException cause = apiEx(HTTP_RATE_LIMITED);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudRateLimitException.class, ex);
        assertEquals(HTTP_RATE_LIMITED, ex.getStatusCode());
    }

    @Test
    void of_whenCode429WithRetryAfterHeader_parsedCorrectly() {
        // given
        HttpHeaders headers = HttpHeaders.of(
                Map.of(RETRY_AFTER_HEADER_LC, List.of(RETRY_AFTER_VALUE)), (k, v) -> true);
        ApiException cause = apiEx(HTTP_RATE_LIMITED, headers);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudRateLimitException.class, ex);
        assertEquals(EXPECTED_RETRY_AFTER_42, ((NoviCloudRateLimitException) ex).getRetryAfterSeconds());
    }

    @Test
    void of_whenCode429NoRetryAfterHeader_returnsZero() {
        // given
        ApiException cause = apiEx(HTTP_RATE_LIMITED);

        // when
        NoviCloudRateLimitException ex =
                (NoviCloudRateLimitException) NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertEquals(EXPECTED_RETRY_AFTER_ZERO, ex.getRetryAfterSeconds());
    }

    @Test
    void of_whenCode429InvalidRetryAfterHeader_returnsZero() {
        // given
        HttpHeaders headers = HttpHeaders.of(
                Map.of(RETRY_AFTER_HEADER_LC, List.of(INVALID_RETRY_AFTER)), (k, v) -> true);
        ApiException cause = apiEx(HTTP_RATE_LIMITED, headers);

        // when
        NoviCloudRateLimitException ex =
                (NoviCloudRateLimitException) NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertEquals(EXPECTED_RETRY_AFTER_ZERO, ex.getRetryAfterSeconds());
    }

    @Test
    void of_whenCode500_returnsServerException() {
        // given
        ApiException cause = apiEx(HTTP_SERVER_ERROR);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudServerException.class, ex);
        assertEquals(HTTP_SERVER_ERROR, ex.getStatusCode());
    }

    @Test
    void of_whenCode503_returnsServerException() {
        // given
        ApiException cause = apiEx(HTTP_SERVICE_UNAVAILABLE);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudServerException.class, ex);
        assertEquals(HTTP_SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @Test
    void of_whenCode422_returnsBaseException() {
        // given
        ApiException cause = apiEx(HTTP_UNPROCESSABLE);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertEquals(NoviCloudException.class, ex.getClass());
        assertEquals(HTTP_UNPROCESSABLE, ex.getStatusCode());
    }

    @Test
    void of_whenCode0WithoutTransportCause_returnsBaseException() {
        // given - code=0 alone (no IOException/InterruptedException cause) is not network
        ApiException cause = apiEx(HTTP_NETWORK_ERROR);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertEquals(NoviCloudException.class, ex.getClass());
        assertEquals(HTTP_NETWORK_ERROR, ex.getStatusCode());
    }

    @Test
    void of_whenCode0WithIOExceptionCause_returnsNetworkException() {
        // given - F-02: transport failure path. The internal mapper in RetryHandler
        // unwraps ApiException's cause to surface the original IOException to of(...).
        java.io.IOException io = new java.io.IOException("connection reset");

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, io, HTTP_NETWORK_ERROR, null, null);

        // then
        assertInstanceOf(NoviCloudNetworkException.class, ex);
        assertEquals(HTTP_NETWORK_ERROR, ex.getStatusCode());
        assertEquals(io, ex.getCause());
    }

    @Test
    void of_whenCode402_returnsAccessException() {
        // given - F-04: REST API option not subscribed
        ApiException cause = apiEx(HTTP_PAYMENT_REQUIRED);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause, cause.getCode(), cause.getResponseHeaders(), cause.getResponseBody());

        // then
        assertInstanceOf(NoviCloudAccessException.class, ex);
        assertEquals(HTTP_PAYMENT_REQUIRED, ex.getStatusCode());
    }

    @Test
    void getErrorDetails_when400WithBothLists_returnsParsed() {
        // given - F-07: typed access to 400 validation envelope
        String body = "{\"status\":400,\"status_opis\":\"Bad request\","
                + "\"dane\":{\"par_niewlasciwe\":[\"foo\",\"bar\"],\"par_bledna_wart\":[\"baz\"]}}";
        NoviCloudException ex = new NoviCloudException(MSG, null, HTTP_BAD_REQUEST, body);

        // when
        java.util.Optional<NoviCloudErrorDetails> details = ex.getErrorDetails();

        // then
        assertTrue(details.isPresent());
        assertEquals(java.util.List.of("foo", "bar"), details.get().parNiewlasciwe());
        assertEquals(java.util.List.of("baz"), details.get().parBlednaWart());
    }

    @Test
    void getErrorDetails_whenBodyMissing_returnsEmpty() {
        // given
        NoviCloudException ex = new NoviCloudException(MSG, null, HTTP_BAD_REQUEST, null);

        // when / then
        assertTrue(ex.getErrorDetails().isEmpty());
    }

    @Test
    void getErrorDetails_whenBodyNotJson_returnsEmpty() {
        // given
        NoviCloudException ex = new NoviCloudException(MSG, null, HTTP_BAD_REQUEST, "not json");

        // when / then
        assertTrue(ex.getErrorDetails().isEmpty());
    }

    @Test
    void getErrorDetails_whenDaneHasNoErrorFields_returnsEmpty() {
        // given - dane is an object but neither par_niewlasciwe nor par_bledna_wart present
        String body = "{\"status\":400,\"dane\":{\"id\":1}}";
        NoviCloudException ex = new NoviCloudException(MSG, null, HTTP_BAD_REQUEST, body);

        // when / then
        assertTrue(ex.getErrorDetails().isEmpty());
    }
}
