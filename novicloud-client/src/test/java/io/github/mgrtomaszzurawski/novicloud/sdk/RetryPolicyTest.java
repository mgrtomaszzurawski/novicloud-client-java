/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RetryPolicyTest {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final long DEFAULT_MAX_RETRY_AFTER_SECS = 60L;
    private static final int MAX_ATTEMPTS_1 = 1;
    private static final int MAX_ATTEMPTS_2 = 2;
    private static final int MAX_ATTEMPTS_4 = 4;
    private static final int MAX_ATTEMPTS_5 = 5;
    private static final long MAX_RETRY_AFTER_SECS_120 = 120L;
    private static final int INVALID_ZERO_ATTEMPTS = 0;
    private static final int INVALID_NEGATIVE_ATTEMPTS = -1;
    private static final long INVALID_ZERO_RETRY_AFTER = 0L;
    private static final long INVALID_NEGATIVE_RETRY_AFTER = -5L;

    @Test
    void defaultPolicy_whenCreated_hasExpectedDefaults() {
        // given / when
        RetryPolicy p = RetryPolicy.defaultPolicy();

        // then
        assertTrue(p.enabled());
        assertTrue(p.retryOn5xx());
        assertEquals(DEFAULT_MAX_ATTEMPTS, p.maxAttempts());
        assertEquals(RetryPolicy.BackoffStrategy.EXPONENTIAL, p.backoffStrategy());
        assertTrue(p.retryOn429());
        assertEquals(DEFAULT_MAX_RETRY_AFTER_SECS, p.maxRetryAfterSeconds());
        // F-01 (1.1.0): default flipped to false - POST is not retried unless opted in
        assertFalse(p.retryPost());
    }

    @Test
    void build_whenAllFieldsOverridden_returnsCustomValues() {
        // given
        var builder = RetryPolicy.builder()
                .enabled(false)
                .retryOn5xx(false)
                .maxAttempts(MAX_ATTEMPTS_5)
                .backoffStrategy(RetryPolicy.BackoffStrategy.FIXED)
                .retryOn429(false)
                .maxRetryAfterSeconds(MAX_RETRY_AFTER_SECS_120)
                .retryPost(false);

        // when
        RetryPolicy p = builder.build();

        // then
        assertFalse(p.enabled());
        assertFalse(p.retryOn5xx());
        assertEquals(MAX_ATTEMPTS_5, p.maxAttempts());
        assertEquals(RetryPolicy.BackoffStrategy.FIXED, p.backoffStrategy());
        assertFalse(p.retryOn429());
        assertEquals(MAX_RETRY_AFTER_SECS_120, p.maxRetryAfterSeconds());
        assertFalse(p.retryPost());
    }

    @Test
    void maxAttempts_whenZero_throwsIllegalArgument() {
        // given
        var builder = RetryPolicy.builder();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> builder.maxAttempts(INVALID_ZERO_ATTEMPTS));
    }

    @Test
    void maxAttempts_whenNegative_throwsIllegalArgument() {
        // given
        var builder = RetryPolicy.builder();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> builder.maxAttempts(INVALID_NEGATIVE_ATTEMPTS));
    }

    @Test
    void maxAttempts_whenOne_accepted() {
        // given / when
        RetryPolicy p = RetryPolicy.builder().maxAttempts(MAX_ATTEMPTS_1).build();

        // then
        assertEquals(MAX_ATTEMPTS_1, p.maxAttempts());
    }

    @Test
    void backoffStrategy_whenNull_fallsBackToExponential() {
        // given / when
        RetryPolicy p = RetryPolicy.builder().backoffStrategy(null).build();

        // then
        assertEquals(RetryPolicy.BackoffStrategy.EXPONENTIAL, p.backoffStrategy());
    }

    @Test
    void maxRetryAfterSeconds_whenZero_throwsIllegalArgument() {
        // given
        var builder = RetryPolicy.builder();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> builder.maxRetryAfterSeconds(INVALID_ZERO_RETRY_AFTER));
    }

    @Test
    void maxRetryAfterSeconds_whenNegative_throwsIllegalArgument() {
        // given
        var builder = RetryPolicy.builder();

        // when / then
        assertThrows(IllegalArgumentException.class, () -> builder.maxRetryAfterSeconds(INVALID_NEGATIVE_RETRY_AFTER));
    }

    @Test
    void build_whenTwoBuildersCreated_theyAreIndependent() {
        // given / when
        RetryPolicy p1 = RetryPolicy.builder().maxAttempts(MAX_ATTEMPTS_2).build();
        RetryPolicy p2 = RetryPolicy.builder().maxAttempts(MAX_ATTEMPTS_4).build();

        // then
        assertEquals(MAX_ATTEMPTS_2, p1.maxAttempts());
        assertEquals(MAX_ATTEMPTS_4, p2.maxAttempts());
    }
}
