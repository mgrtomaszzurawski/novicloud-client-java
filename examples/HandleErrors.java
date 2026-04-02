/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package examples;

import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Towar;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudAuthException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudRateLimitException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudServerException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNetworkException;

/**
 * How to handle SDK errors. All exceptions are unchecked (RuntimeException).
 * Catch order matters: specific before general.
 * The SDK retries automatically on 429/5xx before throwing (see CustomRetryPolicy.java).
 */
public class HandleErrors {

    public static void main(String[] args) {
        try (NoviCloudClient client = NoviCloudClient.create(
                System.getenv("NOVICLOUD_ACCOUNT_NAME"),
                System.getenv("NOVICLOUD_PASSWORD"))) {

            try {
                Towar t = client.towary().getById(999999L);
                System.out.println("Found: " + t.nazwa());
            } catch (NoviCloudNotFoundException e) {
                // 404 - record does not exist
                System.out.println("Not found (HTTP " + e.getStatusCode() + ")");
            } catch (NoviCloudAuthException e) {
                // 401/403 - bad credentials
                System.out.println("Auth failed (HTTP " + e.getStatusCode() + ")");
            } catch (NoviCloudRateLimitException e) {
                // 429 - rate limited (SDK already retried per RetryPolicy)
                System.out.println("Rate limited, retry after " + e.getRetryAfterSeconds() + "s");
            } catch (NoviCloudServerException e) {
                // 5xx - server error (SDK already retried per RetryPolicy)
                System.out.println("Server error (HTTP " + e.getStatusCode() + ")");
                System.out.println("Response: " + e.getResponseBody());
            } catch (NoviCloudNetworkException e) {
                // No HTTP response at all (timeout, connection refused, DNS failure)
                System.out.println("Network error: " + e.getMessage());
            }
        }
    }
}
