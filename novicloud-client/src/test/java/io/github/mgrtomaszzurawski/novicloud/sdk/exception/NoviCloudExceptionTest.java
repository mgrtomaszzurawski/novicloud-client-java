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
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

        // then
        assertInstanceOf(NoviCloudAuthException.class, ex);
        assertEquals(HTTP_UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void of_whenCode403_returnsAuthException() {
        // given
        ApiException cause = apiEx(HTTP_FORBIDDEN);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

        // then
        assertInstanceOf(NoviCloudAuthException.class, ex);
        assertEquals(HTTP_FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void of_whenCode404_returnsNotFoundException() {
        // given
        ApiException cause = apiEx(HTTP_NOT_FOUND);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

        // then
        assertInstanceOf(NoviCloudNotFoundException.class, ex);
        assertEquals(HTTP_NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void of_whenCode410_returnsNotFoundException() {
        // given
        ApiException cause = apiEx(HTTP_GONE);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

        // then
        assertInstanceOf(NoviCloudNotFoundException.class, ex);
        assertEquals(HTTP_GONE, ex.getStatusCode());
    }

    @Test
    void of_whenCode429_returnsRateLimitException() {
        // given
        ApiException cause = apiEx(HTTP_RATE_LIMITED);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

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
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

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
                (NoviCloudRateLimitException) NoviCloudException.of(MSG, cause);

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
                (NoviCloudRateLimitException) NoviCloudException.of(MSG, cause);

        // then
        assertEquals(EXPECTED_RETRY_AFTER_ZERO, ex.getRetryAfterSeconds());
    }

    @Test
    void of_whenCode500_returnsServerException() {
        // given
        ApiException cause = apiEx(HTTP_SERVER_ERROR);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

        // then
        assertInstanceOf(NoviCloudServerException.class, ex);
        assertEquals(HTTP_SERVER_ERROR, ex.getStatusCode());
    }

    @Test
    void of_whenCode503_returnsServerException() {
        // given
        ApiException cause = apiEx(HTTP_SERVICE_UNAVAILABLE);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

        // then
        assertInstanceOf(NoviCloudServerException.class, ex);
        assertEquals(HTTP_SERVICE_UNAVAILABLE, ex.getStatusCode());
    }

    @Test
    void of_whenCode422_returnsBaseException() {
        // given
        ApiException cause = apiEx(HTTP_UNPROCESSABLE);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

        // then
        assertEquals(NoviCloudException.class, ex.getClass());
        assertEquals(HTTP_UNPROCESSABLE, ex.getStatusCode());
    }

    @Test
    void of_whenCode0NetworkError_returnsBaseException() {
        // given
        ApiException cause = apiEx(HTTP_NETWORK_ERROR);

        // when
        NoviCloudException ex = NoviCloudException.of(MSG, cause);

        // then
        assertEquals(NoviCloudException.class, ex.getClass());
        assertEquals(HTTP_NETWORK_ERROR, ex.getStatusCode());
    }
}
