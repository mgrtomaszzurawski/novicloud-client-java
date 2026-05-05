/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk;

/**
 * Immutable retry configuration for all NoviCloud SDK API calls.
 *
 * <p>Attach a custom policy at construction time:
 * <pre>{@code
 * RetryPolicy policy = RetryPolicy.builder()
 *     .maxAttempts(5)
 *     .retryPost(false)
 *     .build();
 *
 * NoviCloudClient client = NoviCloudClient.builder()
 *     .retryPolicy(policy)
 *     .build(accountName, password);
 * }</pre>
 *
 * <p>To disable all retries:
 * <pre>{@code
 * RetryPolicy policy = RetryPolicy.builder().enabled(false).build();
 * }</pre>
 *
 * @see NoviCloudClient.Builder#retryPolicy(RetryPolicy)
 * @since 1.0.0
 */
public final class RetryPolicy {

    /** Backoff strategy between retry attempts. */
    public enum BackoffStrategy {
        /** 1s, 2s, 4s, ... (doubles each attempt, up to maxAttempts). */
        EXPONENTIAL,
        /** Fixed 1s between every attempt. */
        FIXED
    }

    private final boolean enabled;
    private final boolean retryOn5xx;
    private final int maxAttempts;
    private final BackoffStrategy backoffStrategy;
    private final boolean retryOn429;
    private final long maxRetryAfterSeconds;
    private final boolean retryPost;

    private RetryPolicy(Builder builder) {
        this.enabled = builder.enabled;
        this.retryOn5xx = builder.retryOn5xx;
        this.maxAttempts = builder.maxAttempts;
        this.backoffStrategy = builder.backoffStrategy;
        this.retryOn429 = builder.retryOn429;
        this.maxRetryAfterSeconds = builder.maxRetryAfterSeconds;
        this.retryPost = builder.retryPost;
    }

    /** Returns the default policy (all retries enabled, exponential backoff, 3 attempts). */
    public static RetryPolicy defaultPolicy() {
        return new Builder().build();
    }

    /** Returns a new builder pre-populated with default values. */
    public static Builder builder() {
        return new Builder();
    }

    /** Master switch. {@code false} disables all retries regardless of other settings. */
    public boolean enabled() { return enabled; }

    /** Whether to retry on HTTP 5xx server errors. Default: {@code true}. */
    public boolean retryOn5xx() { return retryOn5xx; }

    /**
     * Total number of attempts including the first call (not the number of retries).
     * Minimum effective value is 1. Default: 3.
     */
    public int maxAttempts() { return maxAttempts; }

    /** Backoff strategy between attempts. Default: {@link BackoffStrategy#EXPONENTIAL}. */
    public BackoffStrategy backoffStrategy() { return backoffStrategy; }

    /** Whether to retry on HTTP 429 (rate limit). Default: {@code true}. */
    public boolean retryOn429() { return retryOn429; }

    /**
     * Maximum seconds to wait when the server sends a {@code Retry-After} header.
     * Prevents stalling indefinitely on a malformed header. Default: 60.
     */
    public long maxRetryAfterSeconds() { return maxRetryAfterSeconds; }

    /**
     * Whether POST (create) operations are retried on transient failures.
     *
     * <p><strong>Default: {@code false}</strong> (since 2.0.0).
     *
     * <p>Covers BOTH HTTP 429 (rate limit) and HTTP 5xx for POST. With the
     * default the SDK is at-most-once for POST; the producer's response is the
     * only outcome the caller observes. Setting this to {@code true} re-enables
     * retry for both classes.
     *
     * <p>POST is not idempotent on the NoviCloud REST API: the producer documentation
     * does not guarantee an idempotency-key contract or a global uniqueness guarantee
     * that would make automatic POST retry safe. A POST committed by the server but
     * for which the response was lost - or a server-side 5xx that arrives after the
     * record was already created, or a 429 returned after the create has already
     * begun - can produce duplicate records on retry.
     *
     * <p>Opt in only if the resource you are creating has a unique key whose violation
     * the server reliably surfaces as 4xx (for example, sklep.numer or kod for kartyloj),
     * and you have considered duplicate handling at your call site.
     */
    public boolean retryPost() { return retryPost; }

    /** Builder for {@link RetryPolicy}. */
    public static final class Builder {

        private static final int MIN_ATTEMPTS = 1;
        private static final long MIN_RETRY_AFTER = 0;
        private static final String ERR_MAX_ATTEMPTS = "maxAttempts must be >= 1, got: ";
        private static final String ERR_MAX_RETRY_AFTER = "maxRetryAfterSeconds must be positive, got: ";
        private static final int DEFAULT_MAX_ATTEMPTS = 3;
        private static final long DEFAULT_MAX_RETRY_AFTER_SECONDS = 60L;

        private boolean enabled = true;
        private boolean retryOn5xx = true;
        private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
        private BackoffStrategy backoffStrategy = BackoffStrategy.EXPONENTIAL;
        private boolean retryOn429 = true;
        private long maxRetryAfterSeconds = DEFAULT_MAX_RETRY_AFTER_SECONDS;
        private boolean retryPost;

        private Builder() {
        }

        /** Master switch. Set {@code false} to disable all retries. */
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }

        /** Set {@code false} to surface 5xx errors immediately without retrying. */
        public Builder retryOn5xx(boolean retryOn5xx) { this.retryOn5xx = retryOn5xx; return this; }

        /**
         * Total attempts including the first call. Must be &ge; 1.
         * For example, {@code maxAttempts(1)} means no retries.
         */
        public Builder maxAttempts(int maxAttempts) {
            if (maxAttempts < MIN_ATTEMPTS) {
                throw new IllegalArgumentException(ERR_MAX_ATTEMPTS + maxAttempts);
            }
            this.maxAttempts = maxAttempts;
            return this;
        }

        /** Set the backoff strategy between attempts. */
        public Builder backoffStrategy(BackoffStrategy strategy) {
            this.backoffStrategy = strategy != null ? strategy : BackoffStrategy.EXPONENTIAL;
            return this;
        }

        /** Set {@code false} to not retry on HTTP 429 (disables rate-limit auto-retry). */
        public Builder retryOn429(boolean retryOn429) { this.retryOn429 = retryOn429; return this; }

        /**
         * Cap on the {@code Retry-After} header value in seconds. Prevents stalling on
         * a malformed or extremely large header value. Default: 60.
         */
        public Builder maxRetryAfterSeconds(long seconds) {
            if (seconds <= MIN_RETRY_AFTER) {
                throw new IllegalArgumentException(ERR_MAX_RETRY_AFTER + seconds);
            }
            this.maxRetryAfterSeconds = seconds;
            return this;
        }

        /**
         * Whether to retry POST (create) operations on transient failures.
         *
         * <p>Default since 2.0.0: {@code false} ("at-most-once" POST semantics).
         * Applies to BOTH HTTP 429 (rate limit) and HTTP 5xx. NoviCloud's REST API
         * does not document idempotency guarantees for POST, so retrying a
         * committed-but-lost response - whether it surfaced as 429 or 5xx -
         * can produce duplicate records.
         *
         * <p>Set {@code true} only when the resource has a server-enforced unique
         * key (e.g. {@code kartyloj.kod}) whose violation surfaces as 4xx, and
         * your call site is prepared to handle the conflict.
         *
         * @param retryPost whether to retry POST on 429 and 5xx
         * @return this builder
         */
        public Builder retryPost(boolean retryPost) { this.retryPost = retryPost; return this; }

        /** Builds the {@link RetryPolicy}. */
        public RetryPolicy build() {
            return new RetryPolicy(this);
        }
    }
}
