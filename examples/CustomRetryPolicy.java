/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package examples;

import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.RetryPolicy;

import java.time.Duration;

/**
 * Configure retry behavior: attempts, backoff, timeouts.
 * Default: 3 attempts, exponential backoff, retry on 429 and 5xx.
 */
public class CustomRetryPolicy {

    public static void main(String[] args) {
        // builder() - full control over retry, timeouts, etc.
        // For defaults, use NoviCloudClient.create() instead (see ListProducts.java)
        try (NoviCloudClient client = NoviCloudClient.builder()
                .retryPolicy(RetryPolicy.builder()
                        .maxAttempts(5)
                        .backoffStrategy(RetryPolicy.BackoffStrategy.EXPONENTIAL)
                        .retryPost(true)
                        .maxRetryAfterSeconds(120)
                        .build())
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofMinutes(2))
                .build(System.getenv("NOVICLOUD_ACCOUNT_NAME"),
                        System.getenv("NOVICLOUD_PASSWORD"))) {

            int count = client.towary().count(null);
            System.out.println("Products: " + count);
        }

        // No retry (when you handle retry yourself)
        try (NoviCloudClient noRetry = NoviCloudClient.builder()
                .retryPolicy(RetryPolicy.builder().enabled(false).build())
                .build(System.getenv("NOVICLOUD_ACCOUNT_NAME"),
                        System.getenv("NOVICLOUD_PASSWORD"))) {

            int count = noRetry.towary().count(null);
            System.out.println("Products (no retry): " + count);
        }
    }
}
