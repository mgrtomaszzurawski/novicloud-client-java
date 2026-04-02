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

import java.util.List;
import java.util.ListIterator;

/**
 * Pagination with PagedResult: metadata, random access, bidirectional iteration.
 *
 * list() returns PagedResult<T> which implements Iterable<T> for backward compatibility.
 * It also exposes totalCount(), pageSize(), seek(), listIterator(), fetchFrom().
 */
public class PaginateAllRecords {

    public static void main(String[] args) {
        try (NoviCloudClient client = NoviCloudClient.create(
                System.getenv("NOVICLOUD_ACCOUNT_NAME"),
                System.getenv("NOVICLOUD_PASSWORD"))) {

            PagedResult<Towar> result = client.towary().list(null);

            // Metadata - triggers first page fetch, no separate count() call needed
            System.out.println("Total records: " + result.totalCount());
            System.out.println("Page size:     " + result.pageSize());

            // Forward iteration - backward-compatible with previous Iterable<T> return type
            System.out.println("First 5 records:");
            int shown = 0;
            for (Towar t : result) {
                System.out.printf("  [%d] %s (%s)%n", t.id(), t.nazwa(), t.kod());
                if (++shown >= 5) { break; }
            }

            // Random access - jump directly to record 100, no need to iterate through 1-99
            // Requires server to support ?content=X&start=N (content= from links.self)
            int jumpTo = 100;
            if (result.totalCount() > jumpTo) {
                result.seek(jumpTo);
                ListIterator<Towar> it = result.listIterator();

                Towar forward = it.next();
                System.out.println("Record " + jumpTo + ": " + forward.nazwa());

                Towar back = it.previous();
                System.out.println("Previous (same record): " + back.nazwa());
            }

            // One-shot page fetch at offset - does not change iterator position
            int offset = 50;
            if (result.totalCount() > offset) {
                List<Towar> page = result.fetchFrom(offset);
                System.out.println("Page at offset " + offset + ": " + page.size() + " records");
            }

            // Collect all into memory - forEach uses Iterable<T> compatibility
            TowarQueryBuilder activeFilter = TowarQueryBuilder.builder().aktywny(true).build();
            PagedResult<Towar> active = client.towary().list(activeFilter);
            System.out.println("Active products total: " + active.totalCount());
        }
    }
}
