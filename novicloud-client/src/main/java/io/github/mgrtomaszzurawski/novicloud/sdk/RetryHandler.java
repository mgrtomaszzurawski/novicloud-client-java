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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Executes API calls with automatic retry logic driven by a {@link RetryPolicy}.
 *
 * <p>Used internally by all resource clients. Not intended for direct use by application code.
 *
 * <p>Retry behaviour:
 * <ul>
 *   <li>429 (rate limit) - retried up to {@code maxAttempts} times, honoring the
 *       {@code Retry-After} header if present, capped at
 *       {@link RetryPolicy#maxRetryAfterSeconds()}.</li>
 *   <li>5xx (server error) - retried with exponential or fixed backoff.</li>
 *   <li>4xx (except 429), network errors - not retried.</li>
 *   <li>POST calls - retried only when {@link RetryPolicy#retryPost()} is {@code true}.</li>
 * </ul>
 *
 * @see RetryPolicy
 * @since 1.0.0
 */
public final class RetryHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RetryHandler.class);
    private static final String MSG_UNREACHABLE = "Unreachable: retry loop exhausted without throwing";
    private static final long MILLIS_PER_SECOND = 1000L;
    private static final int FIRST_ATTEMPT = 0;
    private static final int MIN_ATTEMPTS = 1;
    private static final long NO_PREFERRED_WAIT = 0L;
    private static final long BASE_BACKOFF_SECONDS = 1L;
    private static final int NEXT_ATTEMPT_OFFSET = 1;
    private static final int JITTER_DIVISOR = 2;
    private static final int JITTER_UPPER_OFFSET = 1;
    private static final String LOG_RATE_LIMITED = "Rate limited (429), retrying {}/{}: {}";
    private static final String LOG_SERVER_ERROR = "Server error ({}), retrying {}/{}: {}";
    private static final String LOG_RATE_LIMIT_DETAILS = "Rate limit details for '{}'";
    private static final String LOG_SERVER_ERROR_DETAILS = "Server error details for '{}'";
    private static final String LOG_NON_RETRYABLE = "Non-retryable error for '{}': {}";

    private final RetryPolicy policy;

    /**
     * Creates a new handler with the given retry policy.
     *
     * @param policy retry configuration; if {@code null}, {@link RetryPolicy#defaultPolicy()} is used
     */
    public RetryHandler(RetryPolicy policy) {
        this.policy = policy != null ? policy : RetryPolicy.defaultPolicy();
    }

    /**
     * Functional interface for an API call that returns a value and may throw {@link ApiException}.
     *
     * @param <T> type of the return value
     */
    @FunctionalInterface
    public interface ApiCall<T> {
        /** Performs the API call. */
        T call() throws ApiException;
    }

    /**
     * Functional interface for an API call that returns no value and may throw {@link ApiException}.
     */
    @FunctionalInterface
    public interface VoidApiCall {
        /** Performs the API call. */
        void call() throws ApiException;
    }

    /**
     * Executes a non-POST call (GET, PUT, DELETE) with retry per policy.
     *
     * @param <T>     return type
     * @param call    the API call to execute
     * @param message human-readable context used as the exception message on failure
     * @return the result of the call
     * @throws NoviCloudException or a subclass if the call ultimately fails
     */
    public <T> T execute(ApiCall<T> call, String message) {
        return executeInternal(call, message, false);
    }

    /**
     * Executes a POST (create) call with retry per policy.
     * Whether 5xx errors are retried depends on {@link RetryPolicy#retryPost()}.
     *
     * @param <T>     return type
     * @param call    the POST call to execute
     * @param message human-readable context used as the exception message on failure
     * @return the result of the call
     * @throws NoviCloudException or a subclass if the call ultimately fails
     */
    public <T> T executePost(ApiCall<T> call, String message) {
        return executeInternal(call, message, true);
    }

    /**
     * Executes a void PUT or DELETE call with retry per policy.
     *
     * @param call    the API call to execute
     * @param message human-readable context used as the exception message on failure
     * @throws NoviCloudException or a subclass if the call ultimately fails
     */
    public void run(VoidApiCall call, String message) {
        execute(() -> {
            call.call();
            return null;
        }, message);
    }

    private <T> T executeInternal(ApiCall<T> call, String message, boolean isPost) {
        if (!policy.enabled()) {
            try {
                return call.call();
            } catch (ApiException e) {
                throw NoviCloudException.of(message, e);
            }
        }

        int maxAttempts = Math.max(MIN_ATTEMPTS, policy.maxAttempts());
        for (int attempt = FIRST_ATTEMPT; attempt < maxAttempts; attempt++) {
            try {
                return call.call();
            } catch (ApiException e) {
                NoviCloudException sdk = NoviCloudException.of(message, e);
                boolean hasMoreAttempts = attempt < maxAttempts - NEXT_ATTEMPT_OFFSET;

                if (sdk instanceof NoviCloudRateLimitException rl && policy.retryOn429() && hasMoreAttempts) {
                    long preferred = Math.min(rl.getRetryAfterSeconds(), policy.maxRetryAfterSeconds());
                    LOG.info(LOG_RATE_LIMITED, attempt + NEXT_ATTEMPT_OFFSET, maxAttempts, message);
                    LOG.debug(LOG_RATE_LIMIT_DETAILS, message, e);
                    sleep(preferred, attempt, policy.backoffStrategy());
                } else if (sdk instanceof NoviCloudServerException
                        && policy.retryOn5xx()
                        && shouldRetry5xx(isPost)
                        && hasMoreAttempts)
                {
                    LOG.info(LOG_SERVER_ERROR, e.getCode(), attempt + NEXT_ATTEMPT_OFFSET, maxAttempts, message);
                    LOG.debug(LOG_SERVER_ERROR_DETAILS, message, e);
                    sleep(NO_PREFERRED_WAIT, attempt, policy.backoffStrategy());
                } else {
                    LOG.debug(LOG_NON_RETRYABLE, message, sdk.getMessage());
                    throw sdk;
                }
            }
        }
        throw new IllegalStateException(MSG_UNREACHABLE);
    }

    private boolean shouldRetry5xx(boolean isPost) {
        return !isPost || policy.retryPost();
    }

    private static void sleep(long preferredSeconds, int attempt, RetryPolicy.BackoffStrategy strategy) {
        long baseSeconds;
        if (preferredSeconds > NO_PREFERRED_WAIT) {
            baseSeconds = preferredSeconds;
        } else if (strategy == RetryPolicy.BackoffStrategy.EXPONENTIAL) {
            baseSeconds = BASE_BACKOFF_SECONDS << attempt;
        } else {
            baseSeconds = BASE_BACKOFF_SECONDS;
        }
        long baseMillis = baseSeconds * MILLIS_PER_SECOND;
        long jitteredMillis = ThreadLocalRandom.current().nextLong(baseMillis / JITTER_DIVISOR, baseMillis + JITTER_UPPER_OFFSET);
        try {
            Thread.sleep(jitteredMillis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
