/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.rapsprzed;

import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.RaportSprzedazy;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;

/**
 * Client for the {@code rapsprzed} (sales reports) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient} accessors.
 * Implementation lives in the non-exported {@code sdk.internal.resources.rapsprzed}
 * package; external code should depend on this interface only.
 *
 * @since 2.0.0
 */
public interface RapSprzedClient {

    /**
     * Returns the total number of sales reports matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws NoviCloudException on API failure
     */
    int count(RapSprzedQueryBuilder query);

    /**
     * Returns a lazy paginated result over all sales reports matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} of matching records (pages fetched on demand)
     * @throws NoviCloudException on API failure (thrown when the iterator fetches each page)
     */
    PagedResult<RaportSprzedazy> list(RapSprzedQueryBuilder query);
}
