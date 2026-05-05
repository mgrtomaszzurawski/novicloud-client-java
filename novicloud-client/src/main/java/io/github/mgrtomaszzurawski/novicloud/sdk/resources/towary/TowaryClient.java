/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.towary;

import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.Towar;

/**
 * Client for the {@code towary} (products) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient} accessors.
 * Implementation lives in the non-exported {@code sdk.internal.resources.towary}
 * package; external code should depend on this interface only.
 *
 * @since 2.0.0
 */
public interface TowaryClient {

    /**
     * Returns a lazy paginated result over all products matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} of matching records (pages fetched on demand)
     * @throws NoviCloudException on API failure (thrown when the iterator fetches each page)
     */
    PagedResult<Towar> list(TowarQueryBuilder query);

    /**
     * Returns the total number of products matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws NoviCloudException on API failure
     */
    int count(TowarQueryBuilder query);

    /**
     * Fetches a single products record by its numeric ID.
     *
     * @param id the record ID; must not be {@code null}
     * @return the record; never {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws NoviCloudNotFoundException if no products record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    Towar getById(Long id);

    /**
     * Creates a new products record.
     *
     * @param builder the data; must not be {@code null}
     * @return the ID (or code) of the created record, or {@code null} if the server did not return one
     * @throws NoviCloudException on API failure
     */
    String create(TowarCreateBuilder builder);

    /**
     * Updates an existing products record. The identifier field in the builder identifies the record.
     *
     * @param builder the updated data; must not be {@code null}
     * @throws NoviCloudNotFoundException if no products record with the given identifier exists
     * @throws NoviCloudException on other API failure
     */
    void update(TowarUpdateBuilder builder);

    /**
     * Deletes the record with the given ID.
     *
     * @param id the record ID; must not be {@code null}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws NoviCloudNotFoundException if no record with the given ID exists
     * @throws NoviCloudException on other API failure
     */
    void deleteById(Long id);
}
