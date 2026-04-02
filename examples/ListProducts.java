/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package examples;

import io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Towar;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary.TowarQueryBuilder;

/**
 * List products with filtering and lazy pagination.
 *
 * Usage: set NOVICLOUD_ACCOUNT_NAME and NOVICLOUD_PASSWORD env vars, then run.
 */
public class ListProducts {

    public static void main(String[] args) {
        // create() - shorthand with default settings (3 retries, exponential backoff)
        // Use NoviCloudClient.builder() instead to customize retry, timeouts, etc.
        // (see CustomRetryPolicy.java)
        NoviCloudClient client = NoviCloudClient.create(
                System.getenv("NOVICLOUD_ACCOUNT_NAME"),
                System.getenv("NOVICLOUD_PASSWORD"));

        // list() returns PagedResult<T> - totalCount() reads size from the first page response,
        // no separate count() HTTP call needed
        TowarQueryBuilder activeFilter = TowarQueryBuilder.builder()
                .aktywny(true)
                .build();
        PagedResult<Towar> result = client.towary().list(activeFilter);
        System.out.println("Active products: " + result.totalCount());

        // Iterate - PagedResult<T> implements Iterable<T>, pages fetched lazily on demand
        for (Towar t : result) {
            System.out.printf("  [%d] %s (kod=%s, VAT=%d)%n",
                    t.id(), t.nazwa(), t.kod(), t.stawkaVat());
        }

        client.close();
    }
}
