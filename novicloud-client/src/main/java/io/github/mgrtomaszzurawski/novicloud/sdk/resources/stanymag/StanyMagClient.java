/*
 * Copyright (c) 2026 Tomasz Zurawski
 * SPDX-License-Identifier: AGPL-3.0-only
 * https://github.com/mgrtomaszzurawski/novicloud-client-java
 */
package io.github.mgrtomaszzurawski.novicloud.sdk.resources.stanymag;

import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudException;
import io.github.mgrtomaszzurawski.novicloud.sdk.exception.NoviCloudNotFoundException;
import io.github.mgrtomaszzurawski.novicloud.sdk.paging.PagedResult;
import io.github.mgrtomaszzurawski.novicloud.sdk.model.StanMag;

/**
 * Client for the {@code stanymag} (stock levels) endpoint of the NoviCloud API.
 *
 * <p>Obtain an instance from
 * {@link io.github.mgrtomaszzurawski.novicloud.sdk.NoviCloudClient} accessors.
 * Implementation lives in the non-exported {@code sdk.internal.resources.stanymag}
 * package; external code should depend on this interface only.
 *
 * @since 2.0.0
 */
public interface StanyMagClient {

    /**
     * Returns a lazy paginated result over all stock levels matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return a {@link PagedResult} of matching records (pages fetched on demand)
     * @throws NoviCloudException on API failure (thrown when the iterator fetches each page)
     */
    PagedResult<StanMag> list(StanMagQueryBuilder query);

    /**
     * Returns the total number of stock levels matching the given filters.
     *
     * @param query filter parameters, or {@code null} for no filtering
     * @return total record count
     * @throws NoviCloudException on API failure
     */
    int count(StanMagQueryBuilder query);

    /**
     * Fetches stock levels for a specific product across all shops, optionally at a given date.
     *
     * @param idTowaru the product ID; must not be {@code null}
     * @param naDzien optional snapshot date in {@code yyyy-MM-dd} format, or {@code null} for current state
     * @return the list of stock levels per shop, never {@code null}
     * @throws IllegalArgumentException if {@code idTowaru} is {@code null}
     * @throws NoviCloudException on API failure
     */
    java.util.List<StanMag> listByTowar(Long idTowaru, String naDzien);

    /**
     * Fetches stock levels for a specific product in a specific shop, optionally at a given date.
     *
     * @param idTowaru the product ID; must not be {@code null}
     * @param idSklepu the shop ID; must not be {@code null}
     * @param naDzien optional snapshot date in {@code yyyy-MM-dd} format, or {@code null} for current state
     * @return the record; never {@code null}
     * @throws IllegalArgumentException if {@code idTowaru} or {@code idSklepu} is {@code null}
     * @throws NoviCloudNotFoundException if no stock levels record exists for the given keys
     * @throws NoviCloudException on other API failure
     */
    StanMag getByTowarAndSklep(Long idTowaru, Long idSklepu, String naDzien);

    /**
     * Updates an existing stock levels record. The identifier field in the builder identifies the record.
     *
     * @param builder the updated data; must not be {@code null}
     * @throws NoviCloudNotFoundException if no stock levels record with the given identifier exists
     * @throws NoviCloudException on other API failure
     */
    void update(StanMagUpdateBuilder builder);
}
