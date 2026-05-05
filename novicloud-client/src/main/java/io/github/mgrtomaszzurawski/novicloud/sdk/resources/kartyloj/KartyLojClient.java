/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.kartyloj;

import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.KartaLojalnosciowa;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;

/**
 * Client for the {@code kartyloj} (loyalty cards) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient} accessors.
 * Implementation lives in the non-exported {@code sdk.internal.resources.kartyloj}
 * package; external code should depend on this interface only.
 *
 * @since 2.0.0
 */
public interface KartyLojClient {

    /**
     * Returns a lazy paginated result over all loyalty cards matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} of matching records (pages fetched on demand)
     * @throws NoviCloudException on API failure (thrown when the iterator fetches each page)
     */
    PagedResult<KartaLojalnosciowa> list(KartaLojQueryBuilder query);

    /**
     * Returns the total number of loyalty cards matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws NoviCloudException on API failure
     */
    int count(KartaLojQueryBuilder query);

    /**
     * Fetches a single loyalty cards record by its {@code kod} (string identifier).
     *
     * @param kod the record code; must not be {@code null}
     * @return the record; never {@code null}
     * @throws IllegalArgumentException if {@code kod} is {@code null}
     * @throws NoviCloudNotFoundException if no loyalty cards record with the given code exists
     * @throws NoviCloudException on other API failure
     */
    KartaLojalnosciowa getByKod(String kod);

    /**
     * Creates a new loyalty cards record.
     *
     * @param builder the data; must not be {@code null}
     * @return the ID (or code) of the created record, or {@code null} if the server did not return one
     * @throws NoviCloudException on API failure
     */
    String create(KartaLojCreateBuilder builder);

    /**
     * Updates an existing loyalty cards record. The identifier field in the builder identifies the record.
     *
     * @param builder the updated data; must not be {@code null}
     * @throws NoviCloudNotFoundException if no loyalty cards record with the given identifier exists
     * @throws NoviCloudException on other API failure
     */
    void update(KartaLojUpdateBuilder builder);
}
