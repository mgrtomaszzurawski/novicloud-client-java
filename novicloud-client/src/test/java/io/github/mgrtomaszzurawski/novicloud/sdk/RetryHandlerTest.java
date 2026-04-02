/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk;

import io.github.mgrtomaszzurawski.novicloud.client.ApiException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryHandlerTest {

    private static final String OP_NAME = "test";
    private static final String RESULT_OK = "ok";
    private static final String RESULT_DONE = "done";
    private static final String RESULT_CREATED = "created";
    private static final String ERR_MSG = "error";

    private static final int HTTP_401 = 401;
    private static final int HTTP_429 = 429;
    private static final int HTTP_500 = 500;
    private static final int HTTP_503 = 503;
    private static final int MAX_ATTEMPTS_1 = 1;
    private static final int MAX_ATTEMPTS_2 = 2;
    private static final int MAX_ATTEMPTS_3 = 3;

    private static ApiException apiEx(int code) {
        return new ApiException(code, ERR_MSG);
    }

    // -----------------------------------------------------------------------
    // Non-retry paths (no sleep)
    // -----------------------------------------------------------------------

    @Test
    void execute_whenFirstAttemptSucceeds_returnsValue() {
        // given
        RetryHandler handler = new RetryHandler(RetryPolicy.defaultPolicy());

        // when
        String result = handler.execute(() -> RESULT_OK, OP_NAME);

        // then
        assertEquals(RESULT_OK, result);
    }

    @Test
    void execute_whenPolicyDisabled_throwsImmediately() {
        // given
        RetryPolicy policy = RetryPolicy.builder().enabled(false).build();
        RetryHandler handler = new RetryHandler(policy);

        // when / then
        assertThrows(NoviCloudException.class,
                () -> handler.execute(() -> { throw apiEx(HTTP_500); }, OP_NAME));
    }

    @Test
    void execute_whenMaxAttemptsOne_doesNotRetry() {
        // given
        RetryPolicy policy = RetryPolicy.builder().maxAttempts(MAX_ATTEMPTS_1).build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when / then
        assertThrows(NoviCloudServerException.class, () ->
                handler.execute(() -> { calls.incrementAndGet(); throw apiEx(HTTP_500); }, OP_NAME));
        assertEquals(MAX_ATTEMPTS_1, calls.get());
    }

    @Test
    void execute_whenRetryOn429Disabled_throwsImmediately() {
        // given
        RetryPolicy policy = RetryPolicy.builder()
                .retryOn429(false).maxAttempts(MAX_ATTEMPTS_3).build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when / then
        assertThrows(NoviCloudRateLimitException.class, () ->
                handler.execute(() -> { calls.incrementAndGet(); throw apiEx(HTTP_429); }, OP_NAME));
        assertEquals(MAX_ATTEMPTS_1, calls.get());
    }

    @Test
    void execute_whenRetryOn5xxDisabled_throwsImmediately() {
        // given
        RetryPolicy policy = RetryPolicy.builder()
                .retryOn5xx(false).maxAttempts(MAX_ATTEMPTS_3).build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when / then
        assertThrows(NoviCloudServerException.class, () ->
                handler.execute(() -> { calls.incrementAndGet(); throw apiEx(HTTP_500); }, OP_NAME));
        assertEquals(MAX_ATTEMPTS_1, calls.get());
    }

    @Test
    void executePost_whenRetryPostDisabledAnd5xx_throwsImmediately() {
        // given
        RetryPolicy policy = RetryPolicy.builder()
                .retryPost(false).maxAttempts(MAX_ATTEMPTS_3).build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when / then
        assertThrows(NoviCloudServerException.class, () ->
                handler.executePost(() -> { calls.incrementAndGet(); throw apiEx(HTTP_500); }, OP_NAME));
        assertEquals(MAX_ATTEMPTS_1, calls.get());
    }

    @Test
    void executePost_whenRetryPostDisabledAnd429_stillRetries() {
        // given
        // retryPost only suppresses 5xx retries for POST, not 429
        RetryPolicy policy = RetryPolicy.builder()
                .retryPost(false).maxAttempts(MAX_ATTEMPTS_2)
                .backoffStrategy(RetryPolicy.BackoffStrategy.FIXED).build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when - 429 should still retry (and then fail on attempt 2)
        // then
        assertThrows(NoviCloudRateLimitException.class, () ->
                handler.executePost(() -> { calls.incrementAndGet(); throw apiEx(HTTP_429); }, OP_NAME));
        assertEquals(MAX_ATTEMPTS_2, calls.get());
    }

    @Test
    void execute_whenCode401_notRetryableThrowsImmediately() {
        // given
        RetryPolicy policy = RetryPolicy.builder().maxAttempts(MAX_ATTEMPTS_3).build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when / then
        assertThrows(NoviCloudException.class, () ->
                handler.execute(() -> { calls.incrementAndGet(); throw apiEx(HTTP_401); }, OP_NAME));
        assertEquals(MAX_ATTEMPTS_1, calls.get());
    }

    @Test
    void run_whenVoidCallSucceeds_completesNormally() {
        // given
        RetryHandler handler = new RetryHandler(RetryPolicy.defaultPolicy());
        AtomicInteger calls = new AtomicInteger();

        // when / then
        assertDoesNotThrow(() -> handler.run(calls::incrementAndGet, OP_NAME));
        assertEquals(MAX_ATTEMPTS_1, calls.get());
    }

    @Test
    void execute_whenNullPolicy_fallsBackToDefault() {
        // given
        RetryHandler handler = new RetryHandler(null);

        // when
        String result = handler.execute(() -> RESULT_OK, OP_NAME);

        // then
        assertEquals(RESULT_OK, result);
    }

    // -----------------------------------------------------------------------
    // Retry paths (sleeps ~1s each)
    // -----------------------------------------------------------------------

    @Test
    void execute_when429ThenSuccess_retriesAndReturns() {
        // given
        RetryPolicy policy = RetryPolicy.builder()
                .maxAttempts(MAX_ATTEMPTS_2)
                .backoffStrategy(RetryPolicy.BackoffStrategy.FIXED)
                .build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when
        String result = handler.execute(() -> {
            if (calls.incrementAndGet() == MAX_ATTEMPTS_1) {
                throw apiEx(HTTP_429);
            }
            return RESULT_DONE;
        }, OP_NAME);

        // then
        assertEquals(RESULT_DONE, result);
        assertEquals(MAX_ATTEMPTS_2, calls.get());
    }

    @Test
    void execute_when5xxThenSuccess_retriesAndReturns() {
        // given
        RetryPolicy policy = RetryPolicy.builder()
                .maxAttempts(MAX_ATTEMPTS_2)
                .backoffStrategy(RetryPolicy.BackoffStrategy.FIXED)
                .build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when
        String result = handler.execute(() -> {
            if (calls.incrementAndGet() == MAX_ATTEMPTS_1) {
                throw apiEx(HTTP_503);
            }
            return RESULT_DONE;
        }, OP_NAME);

        // then
        assertEquals(RESULT_DONE, result);
        assertEquals(MAX_ATTEMPTS_2, calls.get());
    }

    @Test
    void executePost_whenRetryPostEnabledAnd5xx_retriesSuccessfully() {
        // given
        RetryPolicy policy = RetryPolicy.builder()
                .retryPost(true)
                .maxAttempts(MAX_ATTEMPTS_2)
                .backoffStrategy(RetryPolicy.BackoffStrategy.FIXED)
                .build();
        RetryHandler handler = new RetryHandler(policy);
        AtomicInteger calls = new AtomicInteger();

        // when
        String result = handler.executePost(() -> {
            if (calls.incrementAndGet() == MAX_ATTEMPTS_1) {
                throw apiEx(HTTP_500);
            }
            return RESULT_CREATED;
        }, OP_NAME);

        // then
        assertEquals(RESULT_CREATED, result);
        assertEquals(MAX_ATTEMPTS_2, calls.get());
    }
}
